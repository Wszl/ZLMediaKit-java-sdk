package org.xdove.media.zlk;

import org.junit.Before;
import org.junit.Test;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class ServiceRequestsTest {

    private ServiceRequests req;

    @Before
    public void setUp() throws Exception {
        this.req = new ServiceRequests(
                System.getenv("HOST"),
                System.getenv("SECRET")
        );
        req.setResponseCharset(Charset.forName("GBK"));
    }

    @Test
    public void getApiList() {
        Map<String, Object> ret = this.req.getApiList();
        System.out.print(ret);
    }

    @Test
    public void getThreadsLoad() {
        Map<String, Object> ret = this.req.getThreadsLoad();
        System.out.print(ret);
    }

    @Test
    public void getWorkThreadsLoad() {
        Map<String, Object> ret = this.req.getWorkThreadsLoad();
        System.out.print(ret);
    }

    @Test
    public void getServerConfig() {
        Map<String, Object> ret = this.req.getServerConfig();
        System.out.print(ret);
    }

    @Test
    public void setServerConfig() {
        Map<String, Object> serverParams = new HashMap<>();
        serverParams.put("api.apiDebug", "1");
        Map<String, Object> ret = this.req.setServerConfig(serverParams);
        System.out.print(ret);
    }

    @Test
    public void restartServer() {
        Map<String, Object> ret = this.req.restartServer();
        System.out.print(ret);
    }

    @Test
    public void getMediaList() {
        Map<String, Object> ret = this.req.getMediaList(null, null, null, null);
        System.out.print(ret);
    }

    @Test
    public void closeStream() {
        Map<String, Object> ret = this.req.closeStream(null, null, null, null, null);
        System.out.print(ret);
    }

    @Test
    public void closeStreams() {
        Map<String, Object> ret = this.req.closeStreams(null, null, null, null, null);
        System.out.print(ret);
    }

    @Test
    public void getAllSession() {
        Map<String, Object> ret = this.req.getAllSession(null, null);
        System.out.print(ret);
    }

    @Test
    public void kickSession() {
        Map<String, Object> ret = this.req.kickSession(null);
        System.out.print(ret);
    }

    @Test
    public void kickSessions() {
        Map<String, Object> ret = this.req.kickSessions(null, null);
        System.out.print(ret);
    }

    @Test
    public void addStreamProxy() {
        Map<String, Object> ret = this.req.addStreamProxy("__defaultVhost__", "live", "test",
                "rtmp://live.hkstv.hk.lxdns.com/live/hks2", null, null, null,
                null, null, null,null, null, null,
                null, null, null, null, null);
        System.out.print(ret);
    }

    @Test
    public void delStreamProxy() {
        Map<String, Object> ret = this.req.delStreamProxy("key");
        System.out.print(ret);
    }

    @Test
    public void addFFmpegSource() {
        Map<String, Object> ret = this.req.addFFmpegSource("srcUrl", "rtmp://127.0.0.1/live/stream_form_ffmpeg",
                15, 0, 0, null);
        System.out.print(ret);
    }

    @Test
    public void delFFmpegSource() {
        Map<String, Object> ret = this.req.delFFmpegSource("key");
        System.out.print(ret);
    }

    @Test
    public void isMediaOnline() {
        Map<String, Object> ret = this.req.isMediaOnline("rtsp", "__defaultVhost__", "live", "test");
        System.out.print(ret);
    }

    @Test
    public void getMediaInfo() {
        Map<String, Object> ret = this.req.getMediaInfo("rtsp", "__defaultVhost__", "live", "test");
        System.out.print(ret);
    }

    @Test
    public void getRtpInfo() {
        Map<String, Object> ret = this.req.getRtpInfo("streamId");
        System.out.print(ret);
    }

    @Test
    public void getMp4RecordFile() {
        Map<String, Object> ret = this.req.getMp4RecordFile("vhost", "app", "stream", "period",
                "customized_path");
        System.out.print(ret);
    }

    @Test
    public void startRecord() {
        Map<String, Object> ret = this.req.startRecord(0, "vhost", "app", "stream", "customized_path",
                10);
        System.out.print(ret);
    }

    @Test
    public void stopRecord() {
        Map<String, Object> ret = this.req.stopRecord(0, "vhost", "app", "stream");
        System.out.print(ret);
    }

    @Test
    public void isRecording() {
        Map<String, Object> ret = this.req.isRecording(0, "vhost", "app", "stream");
        System.out.print(ret);
    }

    @Test
    public void getSnap() {
        Map<String, Object> ret = this.req.getSnap("url", 10, 10);
        System.out.print(ret);
    }

    @Test
    public void openRtpServer() {
        Map<String, Object> ret = this.req.openRtpServer(0, 0, "streamId");
        System.out.print(ret);
    }

    @Test
    public void closeRtpServer() {
        Map<String, Object> ret = this.req.closeRtpServer("streamId");
        System.out.print(ret);
    }

    @Test
    public void listRtpServer() {
        Map<String, Object> ret = this.req.listRtpServer();
        System.out.print(ret);
    }

    @Test
    public void startSendRtp() {
        Map<String, Object> ret = this.req.startSendRtp("vhost", "app", "stream", "ssrc",
                "dstUrl", 0, 1, null, null, 1, 1);
        System.out.print(ret);
    }

    @Test
    public void startSendRtpPassive() {
        Map<String, Object> ret = this.req.startSendRtpPassive("vhost", "app", "stream", "ssrc",
                null, 0, 1, null);
        System.out.print(ret);
    }

    @Test
    public void stopSendRtp() {
        Map<String, Object> ret = this.req.stopSendRtp("vhost", "live", "test", "ssrc");
        System.out.print(ret);
    }

    @Test
    public void getStatistic() {
        Map<String, Object> ret = this.req.getStatistic();
        System.out.print(ret);
    }

    @Test
    public void addStreamPusherProxy() {
        Map<String, Object> ret = this.req.addStreamPusherProxy("vhost", "schema", "app", "stream",
                "dstUrl", null, null, null);
        System.out.print(ret);
    }

    @Test
    public void delStreamPusherProxy() {
        Map<String, Object> ret = this.req.delStreamPusherProxy("key");
        System.out.print(ret);
    }
}