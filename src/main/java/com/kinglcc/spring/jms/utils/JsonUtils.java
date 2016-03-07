/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */
package com.kinglcc.spring.jms.utils;

import java.text.SimpleDateFormat;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * JsonUtils
 * <pre>The json tools using Jackson2</pre>
 *
 * @author liaochaochao
 * @since 2016年1月26日 下午8:52:24
 */
public class JsonUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtils.class);
    private static final String DEFAULT_DATEFORMAT = "yyyy-MM-dd HH:mm:ss";

    private static ObjectMapper createMapper(boolean ignoreUnkownProps) {
        ObjectMapper mapper = new ObjectMapper();
        if (ignoreUnkownProps) {
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        }
        mapper.setDateFormat(new SimpleDateFormat(DEFAULT_DATEFORMAT));
        return mapper;
    }

    /**
     * 将json转换成object
     * 
     * @param jsonString json字符串
     * @param clazz 转换后的类型
     * @param ignoreUnkownProps 是否忽略不匹配的属性
     * @return the object converted by json
     */
    public static <T> T toObject(String jsonString, Class<T> clazz, boolean...ignoreUnkownProps) {
        if (StringUtils.isBlank(jsonString) || null == clazz) {
            return null;
        }

        try {
            boolean ignore = isIgnoreUnknownProps(ignoreUnkownProps);
            return createMapper(ignore).<T> readValue(jsonString, clazz);
        } catch (Exception e) {
            LOGGER.error("Deserialize {} error", jsonString, e);
            return null;
        }
    }

    /**
     * 将json转换成object
     * 
     * @param jsonString json字符串
     * @param type 转换后的类型
     * @param ignoreUnkownProps 是否忽略不匹配的属性
     * @return the object converted by json
     */
    public static <T> T toObject(String jsonString, TypeReference<T> type, boolean...ignoreUnkownProps) {
        if (StringUtils.isBlank(jsonString) || null == type) {
            return null;
        }

        try {
            boolean ignore = isIgnoreUnknownProps(ignoreUnkownProps);
            return createMapper(ignore).<T> readValue(jsonString, type);
        } catch (Exception e) {
            LOGGER.error("Deserialize {} error", jsonString, e);
            return null;
        }
    }

    private static boolean isIgnoreUnknownProps(boolean...ignoreUnkownProps) {
        boolean ignore = true;
        if (null != ignoreUnkownProps && ignoreUnkownProps.length > 0) {
            ignore = ignoreUnkownProps[0];
        }
        return ignore;
    }

    public static String toJson(Object obj) {
        try {
            return createMapper(false).writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            LOGGER.error("Serialize {} error", obj, e);
            return null;
        }
    }

}
