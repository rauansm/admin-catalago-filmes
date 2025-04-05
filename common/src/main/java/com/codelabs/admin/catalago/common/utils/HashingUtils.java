package com.codelabs.admin.catalago.common.utils;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import java.nio.ByteBuffer;
import java.util.Base64;

public final class HashingUtils {

    private static final HashFunction CHECKSUM = Hashing.crc32c();

    private HashingUtils() {
    }

    public static String checksum(final byte[] content) {
        int crc32c = Hashing.crc32c().hashBytes(content).asInt();

        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt(crc32c);

        return Base64.getEncoder().encodeToString(buffer.array());
    }
}
