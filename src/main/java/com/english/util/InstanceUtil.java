package com.english.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author XYC
 */
public class InstanceUtil {
    public static final ObjectMapper JSON = new ObjectMapper();

    public static final Logger LOGGER = LoggerFactory.getLogger(InstanceUtil.class);


}
