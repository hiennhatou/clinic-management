package com.ou.utils;

import java.util.HexFormat;

public class HexUtils {
    static public String toHexString(byte[] b) {
        return HexFormat.of().formatHex(b);
    }

    static public byte[] fromHexString(String hex) {
        return HexFormat.of().parseHex(hex);
    }
}
