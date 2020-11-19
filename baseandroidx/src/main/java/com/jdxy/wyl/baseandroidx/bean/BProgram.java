package com.jdxy.wyl.baseandroidx.bean;

public class BProgram {
    private String type;
    private String Id;
    private Data data;

    public String getType() {
        return type == null ? "" : type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return Id == null ? "" : Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {

        private String id;//:节目id
        private String temBytime;//:轮播时间
        private String temIntime;//:轮播间隔时间
        private String temNoops;//:熄屏回调时间
        private String temTryTime;//:下载超时时间
        private String temLink;//节目包下载地址

        private String startTime;//开始播放时间
        private String endTime;//结束播放时间

        public String getStartTime() {
            return startTime == null ? "" : startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getEndTime() {
            return endTime == null ? "" : endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public String getId() {
            return id == null ? "" : id;
        }

        public void setId(String id) {
            this.id = id == null ? "" : id;
        }

        public String getTemBytime() {
            return temBytime == null ? "" : temBytime;
        }

        public void setTemBytime(String temBytime) {
            this.temBytime = temBytime;
        }

        public String getTemIntime() {
            return temIntime == null ? "" : temIntime;
        }

        public void setTemIntime(String temIntime) {
            this.temIntime = temIntime;
        }

        public String getTemNoops() {
            return temNoops == null ? "" : temNoops;
        }

        public void setTemNoops(String temNoops) {
            this.temNoops = temNoops;
        }

        public String getTemTryTime() {
            return temTryTime == null ? "" : temTryTime;
        }

        public void setTemTryTime(String temTryTime) {
            this.temTryTime = temTryTime;
        }

        public String getTemLink() {
            return temLink == null ? "" : temLink;
        }

        public void setTemLink(String temLink) {
            this.temLink = temLink;
        }
    }
}
