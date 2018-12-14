# ftp-tool
FTP客户端工具。从FTP服务器下载文件，支持出错重试。
<br>
<br>
两个重要的接口：<br>
IFtpUtils：FTP工具，将文件上传到FTP，同时也提供从FTP下载文件。提供了两种实现方式，分别是：基于JDK原生包实现、基于Apache工具包实现。<br>
IFtpService：FTP工具与业务结合，可以看作是IFtpUtils的（FTP操作）代理，支持连接池，出错重试等
<br>
<br>
另外两个业务接口：<br>
IFtpFilesDownload与IFtpFileLoad2DB，这里没有提供实现。<br>
IFtpFilesDownload业务接口定义的是，当文件从FTP取得后，要进行的操作；假设是要保存到数据到数据库；<br>
IFtpFileLoad2DB业务接口定义的就将数据保存到数据库的业务。<br>
