package org.toughradius.common;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;

public class Streams
{
    /** 拷贝流内数据,和putBytes方法相同 */
    public static long copy(InputStream input, OutputStream output) throws IOException
    {
        return putBytes(input, output);
    }

    /** 读流中字节数 */
    public static byte[] getBytesFilePath(File file) throws IOException
    {
        if (file == null || !file.isFile() || !file.canRead())
            return null;

        FileInputStream input = new FileInputStream(file);
        int len = input.available();
        int readcount = 0, ret = 0;
        byte[] buf = new byte[len];
        while (readcount < len)
        {
            ret = input.read(buf, readcount, len - readcount);
            if (ret == -1)
                throw new EOFException("按长度读消息时,长度不够即到达流尾端");
            readcount += ret;
        }
        return buf;
    }

    /** 读流成字符串,指定编码 */
    public static String getString(InputStream stream, String charset) throws IOException
    {
        try
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, charset));
            StringWriter writer = new StringWriter();

            char[] chars = new char[256];
            int count = 0;
            while ((count = reader.read(chars)) > 0)
            {
                writer.write(chars, 0, count);
            }

            return writer.toString();
        }
        finally
        {
            if (stream != null)
            {
                stream.close();
            }
        }
    }

    /** 读流中字节数 */
    public static byte[] getBytes(InputStream input, int len) throws IOException
    {
        int readcount = 0, ret = 0;
        byte[] buf = new byte[len];
        while (readcount < len)
        {
            ret = input.read(buf, readcount, len - readcount);
            if (ret == -1)
                throw new EOFException("按长度读消息时,长度不够即到达流尾端");
            readcount += ret;
        }
        return buf;
    }

    /** 读流中资源 */
    public static long putBytes(InputStream input, OutputStream output) throws IOException
    {
        byte[] buffer = new byte[1024];
        long count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer)))
        {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    /** 读CLASSPATH中资源 */
    public static byte[] getBytesClassPath(Class<?> clazz, String path) throws IOException
    {
        InputStream in = clazz.getResourceAsStream(path);
        int len = in.available();
        byte[] buf = new byte[len];
        int off = 0;
        while (off < len)
        {
            int readLen = (len - off > 32) ? 32 : len - off;
            int ret = in.read(buf, off, readLen);
            if (ret == -1)
                throw new EOFException("读[" + path + "]时按长度读消息时,长度不够即到达流尾端");
            off += ret;
        }
        return buf;
    }

    /** 读CLASSPATH中资源 */
    public static void putBytesClassPath(Class<?> clazz, String path, OutputStream output) throws IOException
    {
        InputStream in = clazz.getResourceAsStream(path);
        int len = in.available();
        byte[] buf = new byte[1024];
        int off = 0;
        while (off < len)
        {
            int readLen = (len - off > 1024) ? 1024 : len - off;
            int ret = in.read(buf, 0, readLen);
            if (ret == -1)
                throw new EOFException("读[" + path + "]时按长度读消息时,长度不够即到达流尾端");
            off += ret;

            output.write(buf, 0, ret);
        }
    }

    /** 读流获取一行 */
    public static int readLine(InputStream input, byte[] b, int off, int len) throws IOException
    {
        if (len <= 0)
            return 0;

        int count = 0, c;
        while ((c = input.read()) != -1)
        {
            b[off++] = (byte) c;
            count++;
            if (c == '\n' || count == len)
                break;
        }

        return count > 0 ? count : -1;
    }
}
