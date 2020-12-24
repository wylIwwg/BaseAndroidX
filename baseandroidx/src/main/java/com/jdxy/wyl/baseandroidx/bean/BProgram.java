package com.jdxy.wyl.baseandroidx.bean;

public class BProgram {
    private String id;
    private String type;
    private Data data;

    public String getId() {
        return id == null ? "" : id;
    }

    public void setId(String id) {
        this.id = id == null ? "" : id;
    }

    public String getType() {
        return type == null ? "" : type;
    }

    public void setType(String type) {
        this.type = type == null ? "" : type;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {

        private String temBytime;   //:轮播时间
        private String TemIntime;   //:轮播间隔时间
        private String Id;  //:节目id
        private String TemNoops;    //:熄屏回调时间
        private String TemTrytime;      //:下载超时时间
        private String TemLink;    //节目包下载地址
        private String TemAppname;
        private String StartTime;   //开始播放时间
        private String EndTime; //结束播放时间

        public String getTemBytime() {
            return temBytime == null ? "" : temBytime;
        }

        public void setTemBytime(String temBytime) {
            this.temBytime = temBytime == null ? "" : temBytime;
        }

        public String getTemIntime() {
            return TemIntime == null ? "" : TemIntime;
        }

        public void setTemIntime(String temIntime) {
            TemIntime = temIntime == null ? "" : temIntime;
        }

        public String getId() {
            return Id == null ? "" : Id;
        }

        public void setId(String id) {
            Id = id == null ? "" : id;
        }

        public String getTemNoops() {
            return TemNoops == null ? "" : TemNoops;
        }

        public void setTemNoops(String temNoops) {
            TemNoops = temNoops == null ? "" : temNoops;
        }

        public String getTemTrytime() {
            return TemTrytime == null ? "" : TemTrytime;
        }

        public void setTemTrytime(String temTrytime) {
            TemTrytime = temTrytime == null ? "" : temTrytime;
        }

        public String getTemLink() {
            return TemLink == null ? "" : TemLink;
        }

        public void setTemLink(String temLink) {
            TemLink = temLink == null ? "" : temLink;
        }

        public String getTemAppname() {
            return TemAppname == null ? "" : TemAppname;
        }

        public void setTemAppname(String temAppname) {
            TemAppname = temAppname == null ? "" : temAppname;
        }

        public String getStartTime() {
            return StartTime == null ? "" : StartTime;
        }

        public void setStartTime(String startTime) {
            StartTime = startTime == null ? "" : startTime;
        }

        public String getEndTime() {
            return EndTime == null ? "" : EndTime;
        }

        public void setEndTime(String endTime) {
            EndTime = endTime == null ? "" : endTime;
        }
    }
}
