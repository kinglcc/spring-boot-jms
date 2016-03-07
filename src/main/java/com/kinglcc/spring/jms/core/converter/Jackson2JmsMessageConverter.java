package com.kinglcc.spring.jms.core.converter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.atomic.AtomicReference;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Jackson2JmsMessageConverter
 * <pre>
 * Jackson2 jms message converter:
 * Use Jackson2 to convert java object to {@link javax.jms.Message}.
 * Convert {@link javax.jms.Message} to {@link String}
 * </pre>
 *
 * @author liaochaochao
 * @since 2016年1月29日 下午1:07:01
 */
public class Jackson2JmsMessageConverter implements JmsMessageConverter, BeanClassLoaderAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(Jackson2JmsMessageConverter.class);

    /**
     * Check for Jackson 2.3's overloaded canDeserialize/canSerialize variants with cause reference
     */
    private static final boolean jackson23Available =
            ClassUtils.hasMethod(ObjectMapper.class, "canDeserialize", JavaType.class, AtomicReference.class);

    /**
     * The default encoding used for writing to text messages: UTF-8.
     */
    public static final String DEFAULT_ENCODING = "UTF-8";
    private static final String JMS_MESSAGE_ENCODING_PROP = "messageEncoding@";
    private static final String JMS_MESSAGE_TYPE_PROP = "messageTypeId@";

    private MessageType targetType = MessageType.TEXT;
    private String encoding = DEFAULT_ENCODING;
    private ObjectMapper objectMapper;
    private String encodingPropertyName = JMS_MESSAGE_ENCODING_PROP;
    private String typeIdPropertyName = JMS_MESSAGE_TYPE_PROP;

    private ClassLoader beanClassLoader;

    public Jackson2JmsMessageConverter() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Specify the {@link ObjectMapper} to use instead of using the default.
     */
    public void setObjectMapper(ObjectMapper objectMapper) {
        Assert.notNull(objectMapper, "ObjectMapper must not be null");
        this.objectMapper = objectMapper;
    }

    /**
     * Specify whether {@link #toMessage(Object, Session)} should marshal to a
     * {@link BytesMessage} or a {@link TextMessage}.
     * <p>The default is {@link MessageType#BYTES}, i.e. this converter marshals to
     * a {@link BytesMessage}. Note that the default version of this converter
     * supports {@link MessageType#BYTES} and {@link MessageType#TEXT} only.
     * @see MessageType#BYTES
     * @see MessageType#TEXT
     */
    public void setTargetType(MessageType targetType) {
        Assert.notNull(targetType, "MessageType must not be null");
        this.targetType = targetType;
    }

    /**
     * Specify the encoding to use when converting to and from text-based
     * message body content. The default encoding will be "UTF-8".
     * <p>When reading from a a text-based message, an encoding may have been
     * suggested through a special JMS property which will then be preferred
     * over the encoding set on this MessageConverter instance.
     * @see #setEncodingPropertyName
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     * Specify the name of the JMS message property that carries the encoding from
     * bytes to String and back is BytesMessage is used during the conversion process.
     * <p>Default is none. Setting this property is optional; if not set, UTF-8 will
     * be used for decoding any incoming bytes message.
     * @see #setEncoding
     */
    public void setEncodingPropertyName(String encodingPropertyName) {
        this.encodingPropertyName = encodingPropertyName;
    }

    /**
     * Specify the name of the JMS message property that carries the type id for the
     * contained object: either a mapped id value or a raw Java class name.
     * <p>Default is none. <b>NOTE: This property needs to be set in order to allow
     * for converting from an incoming message to a Java object.</b>
     */
    public void setTypeIdPropertyName(String typeIdPropertyName) {
        this.typeIdPropertyName = typeIdPropertyName;
    }

    @Override
    public Message toMessage(Object object, Session session) throws JMSException, MessageConversionException {
        Message message;
        try {
            switch (this.targetType) {
                case TEXT:
                    message = mapToTextMessage(object, session, this.objectMapper);
                    break;
                case BYTES:
                    message = mapToBytesMessage(object, session, this.objectMapper);
                    break;
                default:
                    message = mapToMessage(object, session, this.objectMapper, this.targetType);
            }
        } catch (IOException ex) {
            throw new MessageConversionException("Could not map JSON object [" + object + "]", ex);
        }
        setTypeIdOnMessage(object, message);
        return message;
    }

    protected void setTypeIdOnMessage(Object object, Message message) throws JMSException {
        if (this.typeIdPropertyName != null) {
            String typeId = object.getClass().getCanonicalName();
            message.setStringProperty(this.typeIdPropertyName, typeId);
        }
    }

    @Override
    public Object fromMessage(Message message) throws JMSException, MessageConversionException {
        try {
            JavaType targetJavaType = getJavaTypeForMessage(message);
            String payload = getPayload(message);
            if (null != targetJavaType) {
                return this.objectMapper.readValue(payload, targetJavaType);
            }
            return new GenericMessage(payload);
        } catch (IOException ex) {
            throw new MessageConversionException("Failed to convert JSON message content", ex);
        }
    }

    /**
     * Map the given object to a {@link TextMessage}.
     * @param object the object to be mapped
     * @param session current JMS session
     * @param objectMapper the mapper to use
     * @return the resulting message
     * @throws JMSException if thrown by JMS methods
     * @throws IOException in case of I/O errors
     * @see Session#createBytesMessage
     */
    protected TextMessage mapToTextMessage(Object object, Session session, ObjectMapper objectMapper)
            throws JMSException, IOException {

        StringWriter writer = new StringWriter();
        objectMapper.writeValue(writer, object);
        return session.createTextMessage(writer.toString());
    }

    /**
     * Map the given object to a {@link BytesMessage}.
     * @param object the object to be mapped
     * @param session current JMS session
     * @param objectMapper the mapper to use
     * @return the resulting message
     * @throws JMSException if thrown by JMS methods
     * @throws IOException in case of I/O errors
     * @see Session#createBytesMessage
     */
    protected BytesMessage mapToBytesMessage(Object object, Session session, ObjectMapper objectMapper)
            throws JMSException, IOException {

        ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
        OutputStreamWriter writer = new OutputStreamWriter(bos, this.encoding);
        objectMapper.writeValue(writer, object);

        BytesMessage message = session.createBytesMessage();
        message.writeBytes(bos.toByteArray());
        if (this.encodingPropertyName != null) {
            message.setStringProperty(this.encodingPropertyName, this.encoding);
        }
        return message;
    }

    /**
     * Template method that allows for custom message mapping.
     * Invoked when {@link #setTargetType} is not {@link MessageType#TEXT} or
     * {@link MessageType#BYTES}.
     * <p>The default implementation throws an {@link IllegalArgumentException}.
     * @param object the object to marshal
     * @param session the JMS Session
     * @param objectMapper the mapper to use
     * @param targetType the target message type (other than TEXT or BYTES)
     * @return the resulting message
     * @throws JMSException if thrown by JMS methods
     * @throws IOException in case of I/O errors
     */
    protected Message mapToMessage(Object object, Session session, ObjectMapper objectMapper, MessageType targetType)
            throws JMSException, IOException {

        throw new IllegalArgumentException("Unsupported message type [" + targetType
                + "]. MappingJackson2MessageConverter by default only supports TextMessages and BytesMessages.");
    }

    /**
     * Convenience method to dispatch to converters for individual message types.
     * 
     * @param message the input message
     * @return the message content
     */
    private String getPayload(Message message) throws JMSException, IOException {
        if (message instanceof TextMessage) {
            return getPayloadFromTextMessage((TextMessage) message);
        } else if (message instanceof BytesMessage) {
            return getPayloadFromBytesMessage((BytesMessage) message);
        } else {
            return getPayloadFromMessage(message);
        }
    }

    /**
     * Convert a TextMessage to a Java Object with the specified type.
     * @param message the input message
     * @return the message text
     * @throws JMSException if thrown by JMS
     * @throws IOException in case of I/O errors
     */
    protected String getPayloadFromTextMessage(TextMessage message) throws JMSException, IOException {
        return message.getText();
    }

    /**
     * Convert a BytesMessage to a Java Object with the specified type.
     * @param message the input message
     * @return the message body
     * @throws JMSException if thrown by JMS
     * @throws IOException in case of I/O errors
     */
    protected String getPayloadFromBytesMessage(BytesMessage message) throws JMSException, IOException {

        String encoding = this.encoding;
        if (this.encodingPropertyName != null && message.propertyExists(this.encodingPropertyName)) {
            encoding = message.getStringProperty(this.encodingPropertyName);
        }
        byte[] bytes = new byte[(int) message.getBodyLength()];
        message.readBytes(bytes);
        try {
            return new String(bytes, encoding);
        } catch (UnsupportedEncodingException ex) {
            throw new MessageConversionException("Cannot convert bytes to String", ex);
        }
    }

    /**
     * Template method that allows for custom message mapping.
     * Invoked when {@link #setTargetType} is not {@link MessageType#TEXT} or
     * {@link MessageType#BYTES}.
     * <p>The default implementation throws an {@link IllegalArgumentException}.
     * @param message the input message
     * @return the message content
     * @throws JMSException if thrown by JMS
     * @throws IOException in case of I/O errors
     */
    protected String getPayloadFromMessage(Message message) throws JMSException, IOException {
        throw new IllegalArgumentException("Unsupported message type [" + message.getClass()
                + "]. default only supports TextMessages and BytesMessages.");
    }

    @Override
    public boolean canConvertFrom(Message message) {
        return message instanceof TextMessage || message instanceof BytesMessage;
    }

    @Override
    public boolean canConvertTo(Object payload) {
        return hasJackson2Converter(payload.getClass()) && canSerialize(payload);
    }

    private boolean hasJackson2Converter(Class<?> clazz) {
        return null != clazz.getAnnotation(Jackson2Converter.class);
    }

    private boolean canSerialize(Object payload) {
        if (!jackson23Available || !LOGGER.isWarnEnabled()) {
            return this.objectMapper.canSerialize(payload.getClass());
        }
        AtomicReference<Throwable> causeRef = new AtomicReference<Throwable>();
        if (this.objectMapper.canSerialize(payload.getClass(), causeRef)) {
            return true;
        }
        Throwable cause = causeRef.get();
        if (cause != null) {
            String msg = "Failed to evaluate serialization for type [" + payload.getClass() + "]";
            if (LOGGER.isDebugEnabled()) {
                LOGGER.warn(msg, cause);
            } else {
                LOGGER.warn(msg + ": " + cause);
            }
        }
        return false;
    }

    protected JavaType getJavaTypeForMessage(Message message) throws JMSException {
        if (null == this.typeIdPropertyName || !message.propertyExists(this.typeIdPropertyName)) {
            return null;
        }
        String typeId = message.getStringProperty(this.typeIdPropertyName);
        if (typeId == null) {
            LOGGER.debug("Could not find type id property [{}]", typeIdPropertyName);
            return null;
        }

        try {
            Class<?> typeClass = ClassUtils.forName(typeId, this.beanClassLoader);
            return this.objectMapper.getTypeFactory().constructType(typeClass);
        } catch (Throwable ex) {
            throw new MessageConversionException("Failed to resolve type id [" + typeId + "]", ex);
        }
    }

    public static final class GenericMessage implements Serializable {

        private static final long serialVersionUID = -5140766195099007531L;

        private final Object content;

        public GenericMessage(Object content) {
            this.content = content;
        }

        public Object getContent() {
            return content;
        }
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

}
