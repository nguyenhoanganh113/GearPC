package com.gearpc.common.dto;

public interface ResultCode {
    String getCode();

    String getKey();

    String getMessage();

    default int getHttpStatusCode() {
        return 500;
    }

    static ResultCode of(final String code, final String message, final int httpStatusCode) {
        return new ResultCode() {
            public String getCode() {
                return code;
            }

            public String getKey() {
                return message;
            }

            public String getMessage() {
                return message;
            }

            public int getHttpStatusCode() {
                return httpStatusCode;
            }
        };
    }

}
