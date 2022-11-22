package org.xdove.media.zlk;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xdove.media.zlk.exception.RequestErrorException;
import org.xdove.media.zlk.exception.RespErrorException;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * ZLMediaKit Java
 * @author Wszl
 * @date 2022-10-20
 */
public class ServiceRequests {

    private final static Logger log = LogManager.getLogger();

    private final CloseableHttpClient client;
    private final String host;
    private final String secret;
    private Charset responseCharset;
    private RequestConfig requestConfig;
    private boolean isUrlEncode;

    /** 获取API列表 */
    public static final String PATH_GET_API_LIST = "/index/api/getApiList";
    /** 获取各epoll(或select)线程负载以及延时 */
    public static final String PATH_GET_THREADS_LOAD = "/index/api/getThreadsLoad";
    /** 获取各后台epoll(或select)线程负载以及延时 */
    public static final String PATH_GET_WORK_THREADS_LOAD = "/index/api/getWorkThreadsLoad";
    /** 获取服务器配置 */
    public static final String PATH_GET_SERVER_CONFIG = "/index/api/getServerConfig";
    /** 设置服务器配置 */
    public static final String PATH_SET_SERVER_CONFIG = "/index/api/setServerConfig";
    /** 重启服务器,只有Daemon方式才能重启，否则是直接关闭！ */
    public static final String PATH_RESTART_SERVER = "/index/api/restartServer";
    /** 获取流列表，可选筛选参数 */
    public static final String PATH_GET_MEDIA_LIST = "/index/api/getMediaList";
    /** 关闭流(目前所有类型的流都支持关闭) */
    public static final String PATH_CLOSE_STREAM = "/index/api/close_stream";
    /** 关闭流(目前所有类型的流都支持关闭) */
    public static final String PATH_CLOSE_STREAMS = "/index/api/close_streams";
    /** 获取所有TcpSession列表(获取所有tcp客户端相关信息) */
    public static final String PATH_GET_ALL_SESSION = "/index/api/getAllSession";
    /** 断开tcp连接，比如说可以断开rtsp、rtmp播放器等 */
    public static final String PATH_KICK_SESSION = "/index/api/kick_session";
    /** 断开tcp连接，比如说可以断开rtsp、rtmp播放器等 */
    public static final String PATH_KICK_SESSIONS = "/index/api/kick_sessions";
    /** 动态添加rtsp/rtmp/hls拉流代理(只支持H264/H265/aac/G711负载) */
    public static final String PATH_ADD_STREAM_PROXY = "/index/api/addStreamProxy";
    /** 关闭拉流代理 */
    public static final String PATH_DEL_STREAM_PROXY = "/index/api/delStreamProxy";
    /** 通过fork FFmpeg进程的方式拉流代理，支持任意协议 */
    public static final String PATH_ADD_FFMPEG_SOURCE = "/index/api/addFFmpegSource";
    /** 关闭ffmpeg拉流代理 */
    public static final String PATH_DEL_FFMPEG_SOURCE = "/index/api/delFFmpegSource";
    /** 判断直播流是否在线 */
    public static final String PATH_IS_MEDIA_ONLINE = "/index/api/isMediaOnline";
    /** 获取流相关信息 */
    public static final String PATH_GET_MEDIA_INFO = "/index/api/getMediaInfo";
    /** 获取rtp代理时的某路ssrc rtp信息 */
    public static final String PATH_GET_RTP_INFO = "/index/api/getRtpInfo";
    /** 搜索文件系统，获取流对应的录像文件列表或日期文件夹列表 */
    public static final String PATH_GET_MP4_RECORD_FILE = "/index/api/getMp4RecordFile";
    /** 开始录制hls或MP4 */
    public static final String PATH_START_RECORD = "/index/api/startRecord";
    /** 停止录制流 */
    public static final String PATH_STOP_RECORD = "/index/api/stopRecord";
    /** 获取流录制状态 */
    public static final String PATH_IS_RECORDING = "/index/api/isRecording";
    /** 获取截图或生成实时截图并返回 */
    public static final String PATH_GET_SNAP = "/index/api/getSnap";
    /** 创建GB28181 RTP接收端口，如果该端口接收数据超时，则会自动被回收(不用调用closeRtpServer接口) */
    public static final String PATH_OPEN_RTP_SERVER = "/index/api/openRtpServer";
    /** 创建GB28181 RTP接收端口，如果该端口接收数据超时，则会自动被回收(不用调用closeRtpServer接口) */
    public static final String PATH_CLOSE_RTP_SERVER = "/index/api/closeRtpServer";
    /** 获取openRtpServer接口创建的所有RTP服务器 */
    public static final String PATH_LIST_RTP_SERVER = "/index/api/listRtpServer";
    /** 作为GB28181客户端，启动ps-rtp推流，支持rtp/udp方式 */
    public static final String PATH_START_SEND_RTP = "/index/api/startSendRtp";
    /** 作为GB28181 Passive TCP服务器；该接口支持rtsp/rtmp等协议转ps-rtp被动推流。 */
    public static final String PATH_START_SEND_RTP_PASSIVE = "/index/api/startSendRtpPassive";
    /** 停止GB28181 ps-rtp推流。 */
    public static final String PATH_STOP_SEND_RTP = "/index/api/stopSendRtp";
    /** 获取主要对象个数统计，主要用于分析内存性能 */
    public static final String PATH_GET_STATISTIC = "/index/api/getStatistic";
    /** 添加rtsp/rtmp主动推流(把本服务器的直播流推送到其他服务器去) */
    public static final String PATH_ADD_STREAM_PUSHER_PROXY = "/index/api/addStreamPusherProxy";
    /** 添加rtsp/rtmp主动推流(把本服务器的直播流推送到其他服务器去) */
    public static final String PATH_DEL_STREAM_PUSHER_PROXY = "/index/api/delStreamPusherProxy";


    public ServiceRequests(String host, String secret) {
        this.client =  HttpClientBuilder
                .create()
                .build();
        this.requestConfig = RequestConfig.custom()
                .build();
        this.host = host;
        this.secret = secret;
        this.responseCharset = Charsets.UTF_8;
        this.isUrlEncode = false;
    }

    public ServiceRequests(CloseableHttpClient client, String host, String secret, boolean isUrlEncode) {
        this.client = client;
        this.requestConfig = RequestConfig.custom()
                .build();
        this.host = host;
        this.secret = secret;
        this.responseCharset = Charsets.UTF_8;
        this.isUrlEncode = isUrlEncode;
    }

    public Charset getResponseCharset() {
        return responseCharset;
    }

    public void setResponseCharset(Charset responseCharset) {
        this.responseCharset = responseCharset;
    }

    /**
     * 获取API列表
     * @return
     */
    public Map<String, Object> getApiList() {
        if (log.isTraceEnabled()) {
            log.trace("request getApiList");
        }
        return this.doGet(PATH_GET_API_LIST, null);
    }

    /**
     * 获取各epoll(或select)线程负载以及延时
     * @return
     */
    public Map<String, Object> getThreadsLoad() {
        if (log.isTraceEnabled()) {
            log.trace("request getThreadsLoad");
        }
        return this.doGet(PATH_GET_THREADS_LOAD, null);
    }

    /**
     * 获取各后台epoll(或select)线程负载以及延时
     * @return
     */
    public Map<String, Object> getWorkThreadsLoad() {
        if (log.isTraceEnabled()) {
            log.trace("request getWorkThreadsLoad");
        }
        return this.doGet(PATH_GET_WORK_THREADS_LOAD, null);
    }

    /**
     * 获取服务器配置
     * @return
     */
    public Map<String, Object> getServerConfig() {
        if (log.isTraceEnabled()) {
            log.trace("request getServerConfig");
        }
        Map<String, Object> param = new TreeMap<>();
        param.put("secret",  this.secret);
        return this.doGet(PATH_GET_SERVER_CONFIG, param);
    }

    /**
     * 设置服务器配置
     * @return
     */
    public Map<String, Object> setServerConfig(Map<String, Object> serverParams) {
        if (log.isTraceEnabled()) {
            log.trace("request setServerConfig");
        }
        Map<String, Object> param = new TreeMap<>(serverParams);
        param.put("secret",  this.secret);
        return this.doGet(PATH_SET_SERVER_CONFIG, param);
    }

    /**
     * 重启服务器,只有Daemon方式才能重启，否则是直接关闭！
     * @return
     */
    public Map<String, Object> restartServer() {
        if (log.isTraceEnabled()) {
            log.trace("request restartServer");
        }
        Map<String, Object> param = new TreeMap<>();
        param.put("secret",  this.secret);
        return this.doGet(PATH_RESTART_SERVER, param);
    }


    /**
     * 获取流列表，可选筛选参数
     * @param schema 筛选协议，例如 rtsp或rtmp
     * @param vhost 筛选虚拟主机，例如__defaultVhost__
     * @param app 筛选应用名，例如 live
     * @param stream 筛选流id，例如 test
     * @return
     */
    public Map<String, Object> getMediaList(String schema, String vhost, String app, String stream) {
        if (log.isTraceEnabled()) {
            log.trace("request getMediaList schema=[{}], vhost=[{}], app=[{}], stream=[{}]",
                    schema, vhost, app, stream);
        }
        Map<String, Object> param = new TreeMap<>();
        if (Objects.nonNull(schema)) {
            param.put("schema", schema);
        }
        if (Objects.nonNull(vhost)) {
            param.put("vhost",  vhost);
        }
        if (Objects.nonNull(app)) {
            param.put("app",  app);
        }
        if (Objects.nonNull(stream)) {
            param.put("stream", stream);
        }
        return this.doGet(PATH_GET_MEDIA_LIST, param);
    }

    /**
     * 关闭流(目前所有类型的流都支持关闭)
     * @deprecated (已过期，请使用close_streams接口替换)
     * @return
     */
    public Map<String, Object> closeStream(String schema, String vhost, String app, String stream, String force) {
        if (log.isTraceEnabled()) {
            log.trace("request closeStream schema=[{}], vhost=[{}], app=[{}], stream=[{}], force=[{}]",
                    schema, vhost, app, stream, force);
        }
        Map<String, Object> param = new TreeMap<>();
        param.put("schema",  schema);
        param.put("vhost",  vhost);
        param.put("app",  app);
        param.put("stream",  stream);
        if (Objects.nonNull(force)) {
            param.put("force",  force);
        }
        return this.doGet(PATH_CLOSE_STREAM, param);
    }

    /**
     * 关闭流(目前所有类型的流都支持关闭)
     * @return
     */
    public Map<String, Object> closeStreams(String schema, String vhost, String app, String stream, String force) {
        if (log.isTraceEnabled()) {
            log.trace("request closeStreams schema=[{}], vhost=[{}], app=[{}], stream=[{}], force=[{}]",
                    schema, vhost, app, stream, force);
        }
        Map<String, Object> param = new TreeMap<>();
        if (Objects.nonNull(schema)) {
            param.put("schema",  schema);
        }
        if (Objects.nonNull(vhost)) {
            param.put("vhost",  vhost);
        }
        if (Objects.nonNull(app)) {
            param.put("app",  app);
        }
        if (Objects.nonNull(stream)) {
            param.put("stream",  stream);
        }
        if (Objects.nonNull(force)) {
            param.put("force",  force);
        }
        return this.doGet(PATH_CLOSE_STREAMS, param);
    }

    /**
     * 获取所有TcpSession列表(获取所有tcp客户端相关信息)
     * @return
     */
    public Map<String, Object> getAllSession(String localPort, String peerIp) {
        if (log.isTraceEnabled()) {
            log.trace("request getAllSession localPort=[{}], peerIp=[{}]",
                    localPort, peerIp);
        }
        Map<String, Object> param = new TreeMap<>();
        if (Objects.nonNull(localPort)) {
            param.put("local_port", localPort);
        }
        if (Objects.nonNull(peerIp)) {
            param.put("peer_ip", peerIp);
        }
        return this.doGet(PATH_GET_ALL_SESSION, param);
    }

    /**
     * 断开tcp连接，比如说可以断开rtsp、rtmp播放器等
     * @return
     */
    public Map<String, Object> kickSession(String id) {
        if (log.isTraceEnabled()) {
            log.trace("request kickSession id=[{}]", id);
        }
        Map<String, Object> param = new TreeMap<>();
        param.put("Id",  id);
        return this.doGet(PATH_KICK_SESSION, param);
    }

    /**
     * 断开tcp连接，比如说可以断开rtsp、rtmp播放器等
     * @return
     */
    public Map<String, Object> kickSessions(String localPort, String peerIp) {
        if (log.isTraceEnabled()) {
            log.trace("request getAllSession localPort=[{}], peerIp=[{}]",
                    localPort, peerIp);
        }
        Map<String, Object> param = new TreeMap<>();
        if (Objects.nonNull(localPort)) {
            param.put("local_port", localPort);
        }
        if (Objects.nonNull(peerIp)) {
            param.put("peer_ip", peerIp);
        }
        return this.doGet(PATH_KICK_SESSIONS, param);
    }

    /**
     * 语动态添加rtsp/rtmp/hls拉流代理(只支持H264/H265/aac/G711负载)
     * @return
     */
    public Map<String, Object> addStreamProxy(String vhost, String app, String stream, String url, Integer retryCount,
                                              Integer rtpType, Integer timeoutSec, Integer enableHls, Integer enableMp4,
                                              Integer enableRtsp, Integer enableRtmp, Integer enableTs,
                                              Integer enableFmp4, Integer enableAudio, Integer addMuteAudio,
                                              String mp4SavePath, Integer mp4MaxSecond, String hlsSavePath
                                              ) {
        if (log.isTraceEnabled()) {
            log.trace("request addStreamProxy vhost=[{}], app=[{}], stream=[{}], url=[{}], retryCount=[{}], rtpType=[{}]," +
                            "timeoutSec=[{}], enableHls=[{}], enableMp4=[{}], enableRtsp=[{}], enableRtmp=[{}], " +
                            "enableTs=[{}], enableFmp4=[{}], enableAudio=[{}], addMuteAudio=[{}], mp4SavePath=[{}], " +
                            "mp4MaxSecond=[{}], hlsSavePath=[{}]", vhost, app, stream, url, retryCount, rtpType,
                            timeoutSec, enableHls, enableMp4, enableRtsp, enableRtmp, enableTs, enableFmp4, enableAudio,
                            addMuteAudio, mp4SavePath, mp4MaxSecond, hlsSavePath);
        }
        Map<String, Object> param = new TreeMap<>();
        param.put("vhost", vhost);
        param.put("app", app);
        param.put("stream", stream);
        param.put("url", url);
        if (Objects.nonNull(retryCount)) {
            param.put("retry_count", retryCount);
        }
        if (Objects.nonNull(rtpType)) {
            param.put("rtp_type", rtpType);
        }
        if (Objects.nonNull(timeoutSec)) {
            param.put("timeout_sec", timeoutSec);
        }
        if (Objects.nonNull(enableHls)) {
            param.put("enable_hls", enableHls);
        }
        if (Objects.nonNull(enableMp4)) {
            param.put("enable_mp4", enableMp4);
        }
        if (Objects.nonNull(enableRtsp)) {
            param.put("enable_rtsp", enableRtsp);
        }
        if (Objects.nonNull(enableRtmp)) {
            param.put("enable_rtmp", enableRtmp);
        }
        if (Objects.nonNull(enableTs)) {
            param.put("enable_ts", enableTs);
        }
        if (Objects.nonNull(enableFmp4)) {
            param.put("enable_fmp4", enableFmp4);
        }
        if (Objects.nonNull(enableAudio)) {
            param.put("enable_audio", enableAudio);
        }
        if (Objects.nonNull(addMuteAudio)) {
            param.put("add_mute_audio", addMuteAudio);
        }
        if (Objects.nonNull(mp4SavePath)) {
            param.put("mp4_save_path", mp4SavePath);
        }
        if (Objects.nonNull(mp4MaxSecond)) {
            param.put("mp4_max_second", mp4MaxSecond);
        }
        if (Objects.nonNull(hlsSavePath)) {
            param.put("hls_save_path", hlsSavePath);
        }
        return this.doGet(PATH_ADD_STREAM_PROXY, param);
    }

    /**
     * 关闭拉流代理
     * 流注册成功后，也可以使用close_streams接口替代
     * @return
     */
    public Map<String, Object> delStreamProxy(String key) {
        if (log.isTraceEnabled()) {
            log.trace("request delStreamProxy key=[{}]", key);
        }
        Map<String, Object> param = new TreeMap<>();
        param.put("key", key);
        return this.doGet(PATH_DEL_STREAM_PROXY, param);
    }

    /**
     * 通过fork FFmpeg进程的方式拉流代理，支持任意协议
     * @return
     */
    public Map<String, Object> addFFmpegSource(String srcUrl, String dstUrl, Integer timeoutMs, Integer enableHls,
                                               Integer enableMp4, String ffmpegCmdKey) {
        if (log.isTraceEnabled()) {
            log.trace("request addFFmpegSource srcUrl=[{}], dstUrl=[{}], timeoutMs=[{}], enableHls=[{}], enableMp4=[{}]," +
                            " ffmpegCmdKey=[{}]", srcUrl, dstUrl, timeoutMs, enableHls, enableMp4, ffmpegCmdKey);
        }
        Map<String, Object> param = new TreeMap<>();
        param.put("src_url", srcUrl);
        param.put("dst_url", dstUrl);
        param.put("timeout_ms", timeoutMs);
        param.put("enable_mp4", enableMp4);
        param.put("enable_hls", enableHls);
        if (Objects.nonNull(ffmpegCmdKey)) {
            param.put("ffmpeg_cmd_key", ffmpegCmdKey);
        }
        return this.doGet(PATH_ADD_FFMPEG_SOURCE, param);
    }

    /**
     * 关闭ffmpeg拉流代理
     * 流注册成功后，也可以使用close_streams接口替代
     * @return
     */
    public Map<String, Object> delFFmpegSource(String key) {
        if (log.isTraceEnabled()) {
            log.trace("request delFFmpegSource key=[{}]", key);
        }
        Map<String, Object> param = new TreeMap<>();
        param.put("key", key);
        return this.doGet(PATH_DEL_FFMPEG_SOURCE, param);
    }

    /**
     * 判断直播流是否在线
     * @deprecated 已过期，请使用getMediaList接口替代
     * @return
     */
    public Map<String, Object> isMediaOnline(String schema, String vhost, String app, String stream) {
        if (log.isTraceEnabled()) {
            log.trace("request isMediaOnline schema=[{}], vhost=[{}], app=[{}], stream=[{}]",
                    schema, vhost, app, stream);
        }
        Map<String, Object> param = new TreeMap<>();
        param.put("schema", schema);
        param.put("vhost", vhost);
        param.put("app", app);
        param.put("stream", stream);
        return this.doGet(PATH_IS_MEDIA_ONLINE, param);
    }

    /**
     * 获取流相关信息
     * @deprecated 已过期，请使用getMediaList接口替代
     * @return
     */
    public Map<String, Object> getMediaInfo(String schema, String vhost, String app, String stream) {
        if (log.isTraceEnabled()) {
            log.trace("request getMediaInfo schema=[{}], vhost=[{}], app=[{}], stream=[{}]",
                    schema, vhost, app, stream);
        }
        Map<String, Object> param = new TreeMap<>();
        param.put("schema", schema);
        param.put("vhost", vhost);
        param.put("app", app);
        param.put("stream", stream);
        return this.doGet(PATH_GET_MEDIA_INFO, param);
    }

    /**
     * 获取rtp代理时的某路ssrc rtp信息
     * @return
     */
    public Map<String, Object> getRtpInfo(String streamId) {
        if (log.isTraceEnabled()) {
            log.trace("request getRtpInfo streamId=[{}]", streamId);
        }
        Map<String, Object> param = new TreeMap<>();
        param.put("streamId", streamId);
        return this.doGet(PATH_GET_RTP_INFO, param);
    }

    /**
     * 获取rtp代理时的某路ssrc rtp信息
     * @return
     */
    public Map<String, Object> getMp4RecordFile(String vhost, String app, String stream, String period,
                                                String customizedPath) {
        if (log.isTraceEnabled()) {
            log.trace("request getMp4RecordFile vhost=[{}], app=[{}], stream=[{}], period=[{}], customizedPath=[{}]",
                    vhost, app, stream, period, customizedPath);
        }
        Map<String, Object> param = new TreeMap<>();
        param.put("vhost", vhost);
        param.put("app", app);
        param.put("stream", stream);
        param.put("period", period);
        if (Objects.nonNull(customizedPath)) {
            param.put("customized_path", customizedPath);
        }
        return this.doGet(PATH_GET_MP4_RECORD_FILE, param);
    }

    /**
     * 开始录制hls或MP4
     * @return
     */
    public Map<String, Object> startRecord(int type, String vhost, String app, String stream, String customizedPath,
                                                Integer maxSecond) {
        if (log.isTraceEnabled()) {
            log.trace("request startRecord type=[{}], vhost=[{}], app=[{}], stream=[{}], customizedPath=[{}]" +
                            ", maxSecond=[{}]", type, vhost, app, stream, customizedPath, maxSecond);
        }
        Map<String, Object> param = new TreeMap<>();
        param.put("type", type);
        param.put("vhost", vhost);
        param.put("app", app);
        param.put("stream", stream);
        if (Objects.nonNull(customizedPath)) {
            param.put("customized_path", customizedPath);
        }
        if (Objects.nonNull(maxSecond)) {
            param.put("max_second", maxSecond);
        }
        return this.doGet(PATH_START_RECORD, param);
    }

    /**
     * 停止录制流
     * @return
     */
    public Map<String, Object> stopRecord(int type, String vhost, String app, String stream) {
        if (log.isTraceEnabled()) {
            log.trace("request startRecord type=[{}], vhost=[{}], app=[{}], stream=[{}]", type, vhost, app, stream);
        }
        Map<String, Object> param = new TreeMap<>();
        param.put("type", type);
        param.put("vhost", vhost);
        param.put("app", app);
        param.put("stream", stream);
        return this.doGet(PATH_STOP_RECORD, param);
    }

    /**
     * 获取流录制状态
     * @return
     */
    public Map<String, Object> isRecording(int type, String vhost, String app, String stream) {
        if (log.isTraceEnabled()) {
            log.trace("request isRecording type=[{}], vhost=[{}], app=[{}], stream=[{}]", type, vhost, app, stream);
        }
        Map<String, Object> param = new TreeMap<>();
        param.put("type", type);
        param.put("vhost", vhost);
        param.put("app", app);
        param.put("stream", stream);
        return this.doGet(PATH_IS_RECORDING, param);
    }

    /**
     * 获取截图或生成实时截图并返回
     * @return
     */
    public Map<String, Object> getSnap(String url, int timeoutSec, int expireSec) {
        if (log.isTraceEnabled()) {
            log.trace("request getSnap url=[{}], timeoutSec=[{}], expireSec=[{}]", url, timeoutSec, expireSec);
        }
        Map<String, Object> param = new TreeMap<>();
        param.put("url", url);
        param.put("timeout_sec", timeoutSec);
        param.put("expire_sec", expireSec);
        return this.doGet(PATH_GET_SNAP, param);
    }

    /**
     * 创建GB28181 RTP接收端口，如果该端口接收数据超时，则会自动被回收(不用调用closeRtpServer接口)
     * @return
     */
    public Map<String, Object> openRtpServer(int port, int enableTcp, String streamId, Integer reUsePort, String ssrc) {
        if (log.isTraceEnabled()) {
            log.trace("request openRtpServer port=[{}], enableTcp=[{}], streamId=[{}]", port, enableTcp, streamId);
        }
        Map<String, Object> param = new TreeMap<>();
        param.put("port", port);
        param.put("tcp_mode", enableTcp);
        param.put("stream_id", streamId);
        if (Objects.nonNull(reUsePort)) {
            param.put("re_use_port", reUsePort);
        }
        if (Objects.nonNull(ssrc)) {
            param.put("ssrc", ssrc);
        }
        return this.doGet(PATH_OPEN_RTP_SERVER, param);
    }

    /**
     * 关闭GB28181 RTP接收端口
     * @return
     */
    public Map<String, Object> closeRtpServer(String streamId) {
        if (log.isTraceEnabled()) {
            log.trace("request closeRtpServer streamId=[{}]", streamId);
        }
        Map<String, Object> param = new TreeMap<>();
        param.put("stream_id", streamId);
        return this.doGet(PATH_CLOSE_RTP_SERVER, param);
    }

    /**
     * 获取openRtpServer接口创建的所有RTP服务器
     * @return
     */
    public Map<String, Object> listRtpServer() {
        if (log.isTraceEnabled()) {
            log.trace("request listRtpServer");
        }
        return this.doGet(PATH_LIST_RTP_SERVER, null);
    }

    /**
     * 作为GB28181客户端，启动ps-rtp推流，支持rtp/udp方式；该接口支持rtsp/rtmp等协议转ps-rtp推流。
     * 第一次推流失败会直接返回错误，成功一次后，后续失败也将无限重试。
     * @return
     */
    public Map<String, Object> startSendRtp(String vhost, String app, String stream, String ssrc, String dstUrl,
                                            int dstPort, int isUdp, Integer srcPort, Integer fromMp4, Integer pt, Integer usePs,
                                            Integer onlyAudio) {
        if (log.isTraceEnabled()) {
            log.trace("request startSendRtp vhost=[{}], app=[{}], stream=[{}], ssrc=[{}], dst_url=[{}], dst_port=[{}], " +
                    "is_udp=[{}], src_port=[{}], fromMp4=[{}] pt=[{}], use_ps=[{}], only_audio=[{}]", vhost,app,stream,ssrc,dstUrl,
                    dstPort,isUdp,srcPort, fromMp4,pt,usePs,onlyAudio);
        }
        Map<String, Object> param = new TreeMap<>();
        param.put("vhost", vhost);
        param.put("app", app);
        param.put("stream", stream);
        param.put("ssrc", ssrc);
        param.put("dst_url", dstUrl);
        param.put("dst_port", dstPort);
        param.put("is_udp", isUdp);
        if (Objects.nonNull(srcPort)) {
            param.put("src_port", srcPort);
        }
        if (Objects.nonNull(fromMp4)) {
            param.put("from_mp4", fromMp4);
        }
        if (Objects.nonNull(pt)) {
            param.put("pt", pt);
        }
        if (Objects.nonNull(usePs)) {
            param.put("use_ps", usePs);
        }
        if (Objects.nonNull(onlyAudio)) {
            param.put("only_audio", onlyAudio);
        }
        return this.doGet(PATH_START_SEND_RTP, param);
    }

    /**
     * 作为GB28181 Passive TCP服务器；该接口支持rtsp/rtmp等协议转ps-rtp被动推流。调用该接口，zlm会启动tcp服务器等待连接请求，
     * 连接建立后，zlm会关闭tcp服务器，然后源源不断的往客户端推流。
     * 第一次推流失败会直接返回错误，成功一次后，后续失败也将无限重试(不停地建立tcp监听，超时后再关闭)。
     * @return
     */
    public Map<String, Object> startSendRtpPassive(String vhost, String app, String stream, String ssrc,
                                                   Integer srcPort, Integer pt, Integer usePs, Integer onlyAudio) {
        if (log.isTraceEnabled()) {
            log.trace("request startSendRtpPassive vhost=[{}], app=[{}], stream=[{}], ssrc=[{}],  " +
                            "src_port=[{}], pt=[{}], use_ps=[{}], only_audio=[{}]", vhost,app,stream,ssrc,
                    srcPort,pt,usePs,onlyAudio);
        }
        Map<String, Object> param = new TreeMap<>();
        param.put("vhost", vhost);
        param.put("app", app);
        param.put("stream", stream);
        param.put("ssrc", ssrc);
        if (Objects.nonNull(srcPort)) {
            param.put("src_port", srcPort);
        }
        if (Objects.nonNull(pt)) {
            param.put("pt", pt);
        }
        if (Objects.nonNull(usePs)) {
            param.put("use_ps", usePs);
        }
        if (Objects.nonNull(onlyAudio)) {
            param.put("only_audio", onlyAudio);
        }
        return this.doGet(PATH_START_SEND_RTP_PASSIVE, param);
    }

    /**
     * 停止GB28181 ps-rtp推流
     * @return
     */
    public Map<String, Object> stopSendRtp(String vhost, String app, String stream, String ssrc) {
        if (log.isTraceEnabled()) {
            log.trace("request stopSendRtp vhost=[{}], app=[{}], stream=[{}], ssrc=[{}]", vhost,app,stream,ssrc);
        }
        Map<String, Object> param = new TreeMap<>();
        param.put("vhost", vhost);
        param.put("app", app);
        param.put("stream", stream);
        if (Objects.nonNull(ssrc)) {
            param.put("ssrc", ssrc);
        }
        return this.doGet(PATH_STOP_SEND_RTP, param);
    }

    /**
     * 获取主要对象个数统计，主要用于分析内存性能
     * @return
     */
    public Map<String, Object> getStatistic() {
        if (log.isTraceEnabled()) {
            log.trace("request getStatistic");
        }
        return this.doGet(PATH_GET_STATISTIC, null);
    }

    /**
     * 添加rtsp/rtmp主动推流(把本服务器的直播流推送到其他服务器去)
     * @return
     */
    public Map<String, Object> addStreamPusherProxy(String vhost, String schema,String app, String stream, String dstUrl,
                                                    Integer retryCount, Integer rtpType, Integer timeoutSec) {
        if (log.isTraceEnabled()) {
            log.trace("request addStreamPusherProxy vhost=[{}], schema=[{}], app=[{}], stream=[{}], " +
                            "dstUrl=[{}], retryCount=[{}], rtpType=[{}], timeoutSec=[{}]", vhost, schema, app,
                            stream, dstUrl, retryCount, rtpType, timeoutSec);
        }
        Map<String, Object> param = new TreeMap<>();
        param.put("vhost", vhost);
        param.put("schema", schema);
        param.put("app", app);
        param.put("stream", stream);
        param.put("dst_url", dstUrl);
        if (Objects.nonNull(retryCount)) {
            param.put("retry_count", retryCount);
        }
        if (Objects.nonNull(rtpType)) {
            param.put("rtp_type", rtpType);
        }
        if (Objects.nonNull(timeoutSec)) {
            param.put("timeout_sec", timeoutSec);
        }
        return this.doGet(PATH_ADD_STREAM_PUSHER_PROXY, null);
    }

    /**
     * 关闭推流
     * 可以使用close_streams接口关闭源直播流也可以停止推流
     * @return
     */
    public Map<String, Object> delStreamPusherProxy(String key) {
        if (log.isTraceEnabled()) {
            log.trace("request addStreamPusherProxy key=[{}]", key);
        }
        Map<String, Object> param = new TreeMap<>();
        param.put("key", key);
        return this.doGet(PATH_DEL_STREAM_PUSHER_PROXY, null);
    }

    private String combPath(String path, Map<String, Object> p) {
        List<NameValuePair> paramPairs = new ArrayList<>();
        if (Objects.nonNull(p)) {
            p.forEach((k, v) -> {
                paramPairs.add(new BasicNameValuePair(k, v.toString()));
            });
        }
        paramPairs.add(new BasicNameValuePair("secret", this.secret));
        String paramStr = null;
        if (isUrlEncode) {
            paramStr = URLEncodedUtils.format(paramPairs, StandardCharsets.UTF_8);
            return this.host + path + "?" + paramStr;
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(this.host).append(path).append("?");
            paramPairs.forEach(e -> sb.append(e.getName())
                    .append("=")
                    .append(e.getValue())
                    .append("&"));
            return sb.substring(0, sb.length() - 1);
        }
    }

    private Map<String, Object> doGet(String path, Map<String, Object> p) {
        String url = combPath(path, p);
        HttpGet get = new HttpGet(url);
        get.setConfig(requestConfig);
        if (log.isDebugEnabled()) {
            log.debug("get request path=[{}]]", url);
        }

        try {
            final HttpResponse response = client.execute(get);
            String respContent = IOUtils.toString(response.getEntity().getContent(),
                    Objects.isNull(response.getEntity().getContentEncoding()) ? this.responseCharset.displayName() : response.getEntity().getContentEncoding().getValue());
            if (log.isDebugEnabled()) {
                log.debug("path=[{}], params=[{}], response status=[{}] content=[{}]", path, p,
                        response.getStatusLine().getStatusCode(), respContent);
            }
            final JSONObject jr = JSONObject.parseObject(respContent);
            if (!Integer.valueOf(0).equals(jr.get("code"))) {
                throw new RespErrorException(respContent);
            }
            return jr.getInnerMap();
        } catch (IOException e) {
            log.info("path=[{}], params=[{}] error.", path, p, e);
            throw new RequestErrorException(e);
        }
    }



}
