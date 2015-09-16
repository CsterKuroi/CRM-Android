package com.melnykov.fab.sample.tools;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

public class MultipartThreadDownloador
{

    /**
     * 需要下载资源的地址
     */
    private String urlStr;
    /**
     * 下载的文件
     */
    private File localFile;
    /**
     * 需要下载文件的存放的本地文件夹路径
     */
    private String dirStr;
    /**
     * 存储到本地的文件名
     */
    private String filename;

    /**
     * 开启的线程数量
     */
    private int threadCount;
    /**
     * 下载文件的大小
     */
    private long fileSize;

    public MultipartThreadDownloador(String urlStr, String dirStr,
                                     String filename, int threadCount)
    {
        this.urlStr = urlStr;
        this.dirStr = dirStr;
        this.filename = filename;
        this.threadCount = threadCount;
    }

    public void download() throws IOException
    {
        createFileByUrl();

        /**
         * 计算每个线程需要下载的数据长度
         */
        long block = fileSize % threadCount == 0 ? fileSize / threadCount
                : fileSize / threadCount + 1;

        for (int i = 0; i < threadCount; i++)
        {
            long start = i * block;
            long end = start + block >= fileSize ? fileSize : start + block - 1;

            new DownloadThread(new URL(urlStr), localFile, start, end).start();
        }

    }

    /**
     * 根据资源的URL获取资源的大小，以及在本地创建文件
     */
    public void createFileByUrl() throws IOException
    {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(15 * 1000);
        conn.setRequestMethod("GET");
        conn.setRequestProperty(
                "Accept",
                "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
        conn.setRequestProperty("Accept-Language", "zh-CN");
        conn.setRequestProperty("Referer", urlStr);
        conn.setRequestProperty("Charset", "UTF-8");
        conn.setRequestProperty(
                "User-Agent",
                "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
        conn.setRequestProperty("Connection", "Keep-Alive");
        conn.connect();

        if (conn.getResponseCode() == 200)
        {
            this.fileSize = conn.getContentLength();// 根据响应获取文件大小
            if (fileSize <= 0)
                throw new RuntimeException(
                        "the file that you download has a wrong size ... ");
            File dir = new File(dirStr);
            if (!dir.exists())
                dir.mkdirs();
            this.localFile = new File(dir, filename);
            RandomAccessFile raf = new RandomAccessFile(this.localFile, "rw");
            raf.setLength(fileSize);
            raf.close();

            System.out.println("需要下载的文件大小为 :" + this.fileSize + " , 存储位置为： "
                    + dirStr + "/" + filename);

        } else
        {
            throw new RuntimeException("url that you conneted has error ...");
        }
    }

    private class DownloadThread extends Thread
    {
        /**
         * 下载文件的URI
         */
        private URL url;
        /**
         * 存的本地路径
         */
        private File localFile;
        /**
         * 是否结束
         */
        private boolean isFinish;
        /**
         * 开始的位置
         */
        private Long startPos;
        /**
         * 结束位置
         */
        private Long endPos;

        public DownloadThread(URL url, File savefile, Long startPos, Long endPos)
        {
            this.url = url;
            this.localFile = savefile;
            this.startPos = startPos;
            this.endPos = endPos;
        }

        @Override
        public void run()
        {
            System.out.println(Thread.currentThread().getName() + "开始下载...");
            try
            {
                HttpURLConnection conn = (HttpURLConnection) url
                        .openConnection();
                conn.setConnectTimeout(15 * 1000);
                conn.setRequestMethod("GET");
                conn.setRequestProperty(
                        "Accept",
                        "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
                conn.setRequestProperty("Accept-Language", "zh-CN");
                conn.setRequestProperty("Referer", url.toString());
                conn.setRequestProperty("Charset", "UTF-8");
                conn.setRequestProperty("Range", "bytes=" + startPos + "-"
                        + endPos);// 设置获取实体数据的范围

                conn.setRequestProperty(
                        "User-Agent",
                        "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.connect();

                /**
                 * 代表服务器已经成功处理了部分GET请求
                 */
                if (conn.getResponseCode() == 206)
                {
                    InputStream is = conn.getInputStream();
                    int len = 0;
                    byte[] buf = new byte[1024];

                    RandomAccessFile raf = new RandomAccessFile(localFile,
                            "rwd");
                    raf.seek(startPos);
                    while ((len = is.read(buf)) != -1)
                    {
                        raf.write(buf, 0, len);
                    }
                    raf.close();
                    is.close();
                    System.out.println(Thread.currentThread().getName()
                            + "完成下载  ： " + startPos + " -- " + endPos);
                    this.isFinish = true;
                } else
                {
                    throw new RuntimeException(
                            "url that you conneted has error ...");
                }
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }

    }



}