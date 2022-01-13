package com.hou.stream.zerocopy;

import org.junit.Test;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class ZeroCopy {

    /**
     * 底层用mmap
     * @throws IOException
     */
    @Test
    public void mmap() throws IOException {
        FileChannel readChannel = FileChannel.open(Paths.get("D:\\2021-dev\\Fantasy\\Stream\\src\\main\\resources\\jay.txt"), StandardOpenOption.READ);
        MappedByteBuffer buffer = readChannel.map(FileChannel.MapMode.READ_ONLY, 0,  10);
        FileChannel writeChannel = FileChannel.open(Paths.get("D:\\2021-dev\\Fantasy\\Stream\\src\\main\\resources\\target.txt"), StandardOpenOption.WRITE);
        // 数据传输
        writeChannel.write(buffer);
        readChannel.close();
        writeChannel.close();
    }

    /**
     * 底层用sendfile
     */
    @Test
    public void sendfile() throws IOException {
        FileChannel readChannel = FileChannel.open(Paths.get("D:\\2021-dev\\Fantasy\\Stream\\src\\main\\resources\\jay.txt"), StandardOpenOption.READ);
        FileChannel writeChannel = FileChannel.open(Paths.get("D:\\2021-dev\\Fantasy\\Stream\\src\\main\\resources\\fantasy.txt"), StandardOpenOption.CREATE_NEW,StandardOpenOption.APPEND);
        readChannel.transferTo(0,readChannel.size(),writeChannel);
        readChannel.close();
        writeChannel.close();
    }
}
