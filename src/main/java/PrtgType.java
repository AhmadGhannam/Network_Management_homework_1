import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class PrtgType {

    @JsonProperty("prtg-version")
    private String prtgversion;

    private int treesize;
    @JsonProperty("sensors")
    private List<Sensors> sensors;

    public String getPrtgversion() {
        return prtgversion;
    }

    public void setPrtgversion(String prtgversion) {
        this.prtgversion = prtgversion;
    }

    public int getTreesize() {
        return treesize;
    }

    public void setTreesize(int treesize) {
        this.treesize = treesize;
    }

    public List<Sensors> getSensors() {
        return sensors;
    }

    public void setSensors(List<Sensors> sensors) {
        System.out.println(sensors);
        this.sensors = sensors;
    }

    public static class Sensors {

        private String group;
        private String group_raw;
        private String device;
        private String device_raw;
        private String sensor;
        private String sensor_raw;
        private String status;
        private int status_raw;

        public String getGroup() {
            return group;
        }

        public void setGroup(String group) {
            this.group = group;
        }

        public String getGroup_raw() {
            return group_raw;
        }

        public void setGroup_raw(String group_raw) {
            this.group_raw = group_raw;
        }

        public String getDevice() {
            return device;
        }

        public void setDevice(String device) {
            this.device = device;
        }

        public String getDevice_raw() {
            return device_raw;
        }

        public void setDevice_raw(String device_raw) {
            this.device_raw = device_raw;
        }

        public String getSensor() {
            return sensor;
        }

        public void setSensor(String sensor) {
            this.sensor = sensor;
        }

        public String getSensor_raw() {
            return sensor_raw;
        }

        public void setSensor_raw(String sensor_raw) {
            this.sensor_raw = sensor_raw;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public int getStatus_raw() {
            return status_raw;
        }

        public void setStatus_raw(int status_raw) {
            this.status_raw = status_raw;
        }
    }

}
