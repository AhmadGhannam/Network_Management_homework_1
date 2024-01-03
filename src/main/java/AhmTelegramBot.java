import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;


public class AhmTelegramBot extends TelegramLongPollingBot {

    private boolean checkSensorDeviceStatus =false;
    private boolean checkDeviceStatus=false;
    private boolean checkDeviceInfo=false;
    private boolean checkSensorInfo=false;

    public String QueryStatusOfSensors(String Device, String Sensor){
        String prtgApiUrl = "http://127.0.0.1/api/table.json?content=sensors&username=prtgadmin&passhash=259334832"; // Replace with your PRTG API URL
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(prtgApiUrl))
                .header("Content-Type", "application/json")
                .build();
        String status="";
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            ObjectMapper objectMapper = new ObjectMapper();
            String s=response.body();
            PrtgType prtgType = objectMapper.readValue(s, PrtgType.class);
            List<PrtgType.Sensors> sensorsList = prtgType.getSensors();
            for (PrtgType.Sensors sensor : sensorsList) {
                if(sensor.getSensor().equals(Sensor)&&sensor.getDevice().equals(Device)){
                    status=sensor.getStatus();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if(status.equals(""))return "your device or sensor you entered not exist";
        return "the status of the sensor "+ Sensor +" for device "+Device+" is: "+status;
    }

    public String QueryStatusOfDevices(String name){
        String prtgApiUrl = "http://127.0.0.1/api/table.json?content=devices&username=prtgadmin&passhash=259334832"; // Replace with your PRTG API URL
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(prtgApiUrl))
                .header("Content-Type", "application/json")
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            ObjectMapper objectMapper = new ObjectMapper();
            String s=response.body();
            DevicesType devicesType = objectMapper.readValue(s, DevicesType.class);
            List<DevicesType.Device> devicesList = devicesType.getDevices();
            String dev="";
            for (DevicesType.Device device : devicesList) {
                if(name.equals(device.getDevice())){
                    dev=name;
                    System.out.println(dev);return "the status of device "+name+" is "+device.getStatus();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return "the device you entered not exist";
    }

    public String SearchForSensorById(String id){
        String ID="";boolean check=false;
        for (int i = 0; i < id.length(); i++) {
            if(id.charAt(i)=='.'){check=true;break;}
            ID+=id.charAt(i);
        }
        System.out.println("111: "+ID);
        String prtgApiUrl = "http://127.0.0.1:1615/api/v2/sensors/"+ID+"/data"; // Replace with your PRTG API URL
        String apiKey = "VKNWNO5N4UQ4DCRKWELTSPW6I7OS2X6WJ3B4MPAWMM======"; // Replace with your API key
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(prtgApiUrl))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .build();
        String ans="";
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String s=response.body();
            JSONArray jsonArray=new JSONArray(s);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                JSONObject channelObject = jsonObject.getJSONObject("channel");
                JSONObject measureObject = jsonObject.getJSONObject("last_measurement");
                String idCopy=id;
                String idObject = channelObject.getString("id");
                if(!check){
                    for (int j = 0; j < idObject.length(); j++) {
                        if(idObject.charAt(j)=='.'){
                            for (int k = j; k < idObject.length(); k++) {
                                    idCopy+=idObject.charAt(k);
                            }
                        }
                    }
                }
                if(idObject.equals(id)||idObject.equals(idCopy)){
                    ans="the value of sensor "+channelObject.getString("name")
                            +" is "+measureObject.getInt("value")+" and unit type is "+
                            jsonObject.getJSONObject("unit").getString("type");
                    break;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("ans: "+ans);
        if(ans=="")return "this id is invalid or not exist";
        return ans;
    }

    public String SearchForDeviceById(String id){

        String prtgApiUrl = "http://127.0.0.1:1615/api/v2/devices/"+id;
        String apiKey = "VKNWNO5N4UQ4DCRKWELTSPW6I7OS2X6WJ3B4MPAWMM======";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(prtgApiUrl))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .build();
        String ans="";
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String s=response.body();
            JSONObject jsonObject=new JSONObject(s);
            ans="the name of device for id you entered is "+jsonObject.getString("name")+
                    " and the type of this device is "+jsonObject.getString("type")+
                    " kind of this device is "+jsonObject.getString("kind")+
            " and the value of priority is "+jsonObject.getJSONObject("basic").getString("priority");
        }
        catch (Exception e) {
            ans="";
            e.printStackTrace();
        }
        if(ans=="")return "this id is invalid or not exist";
        return ans;
    }



    @Override
    public void onUpdateReceived(Update update) {
        String ResponseMessage="";
        System.out.println(update.getMessage().getText());
        String telegramMessage=update.getMessage().getText();
        /*
        * Dynamic Queries
        * */
            if(telegramMessage.equals("/sensor_status")){
            checkSensorDeviceStatus =true;
            String message="please put the device and sensor name in this format: deviceName - sensorName";
            SendMessage response=new SendMessage();
            response.setChatId(update.getMessage().getChatId().toString());
            response.setText(message);
            try{
                execute(response);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
            else if(telegramMessage.charAt(0)!='/'&& checkSensorDeviceStatus){
                String sen="",dev="";boolean check1=false,check2=false;
                for(int i=0;i<telegramMessage.length();i++){
                    if(i<telegramMessage.length()-1){
                        if(telegramMessage.charAt(i+1)!='-'&&!check1){
                            dev+=telegramMessage.charAt(i);
                        }
                        else check1=true;
                    }
                    if(i>1){
                        if(telegramMessage.charAt(i-2)=='-'||check2){
                            check2=true;
                            sen+=telegramMessage.charAt(i);
                        }
                    }
                }
                String message= QueryStatusOfSensors(dev,sen);
                SendMessage response=new SendMessage();
                response.setChatId(update.getMessage().getChatId().toString());
                response.setText(message);
                try{
                    execute(response);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                checkSensorDeviceStatus =false;
            }

            else if(telegramMessage.equals("/device_status")){
                checkDeviceStatus =true;
                String message="please put the name of the device you want to see the status for";
                SendMessage response=new SendMessage();
                response.setChatId(update.getMessage().getChatId().toString());
                response.setText(message);
                try{
                    execute(response);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            else if(telegramMessage.charAt(0)!='/'&& checkDeviceStatus){
                String dev=telegramMessage;
                String message= QueryStatusOfDevices(dev);
                SendMessage response=new SendMessage();
                response.setChatId(update.getMessage().getChatId().toString());
                response.setText(message);
                try{
                    execute(response);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                checkDeviceStatus =false;
            }

            else if(telegramMessage.equals("/info_device_byid")){
                checkDeviceInfo =true;
                String message="please put the id of the device you want to see the general inforamation about it";
                SendMessage response=new SendMessage();
                response.setChatId(update.getMessage().getChatId().toString());
                response.setText(message);
                try{
                    execute(response);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            else if(telegramMessage.charAt(0)!='/'&& checkDeviceInfo){
                String dev=telegramMessage;
                String message= SearchForDeviceById(dev);
                SendMessage response=new SendMessage();
                response.setChatId(update.getMessage().getChatId().toString());
                response.setText(message);
                try{
                    execute(response);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                checkDeviceInfo =false;
            }

            else if(telegramMessage.equals("/info_sensor_byid")){
                checkSensorInfo =true;
                String message="please put the id of the sensor you want to see the general information about it";
                SendMessage response=new SendMessage();
                response.setChatId(update.getMessage().getChatId().toString());
                response.setText(message);
                try{
                    execute(response);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            else if(telegramMessage.charAt(0)!='/'&& checkSensorInfo){
                String dev=telegramMessage;
                String message= SearchForSensorById(dev);
                SendMessage response=new SendMessage();
                response.setChatId(update.getMessage().getChatId().toString());
                response.setText(message);
                try{
                    execute(response);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                checkSensorInfo =false;
            }
/*
* IOU Device Query
* */
            else if(telegramMessage.equals("/traffic_total_iou1")){
                String message= SearchForSensorById("2177.0");
                SendMessage response=new SendMessage();
                response.setChatId(update.getMessage().getChatId().toString());
                response.setText(message);
                try{
                    execute(response);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            else if(telegramMessage.equals("/cpu1_iou1")){
                String message= SearchForSensorById("2178");
                SendMessage response=new SendMessage();
                response.setChatId(update.getMessage().getChatId().toString());
                response.setText(message);
                try{
                    execute(response);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            else if(telegramMessage.equals("/available_memory_iou1")){
                String message= SearchForSensorById("2179");
                SendMessage response=new SendMessage();
                response.setChatId(update.getMessage().getChatId().toString());
                response.setText(message);
                try{
                    execute(response);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            else if(telegramMessage.equals("/system_uptime_iou1")){
                String message= SearchForSensorById("2176");
                SendMessage response=new SendMessage();
                response.setChatId(update.getMessage().getChatId().toString());
                response.setText(message);
                try{
                    execute(response);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            /*
             Probe Device Query
             */
            else if(telegramMessage.equals("/cpu_load_probe")){
                String message= SearchForSensorById("1002.5");
                SendMessage response=new SendMessage();
                response.setChatId(update.getMessage().getChatId().toString());
                response.setText(message);
                try{
                    execute(response);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            else if(telegramMessage.equals("/free_memory_probe")){
                String message= SearchForSensorById("1002.3");
                SendMessage response=new SendMessage();
                response.setChatId(update.getMessage().getChatId().toString());
                response.setText(message);
                try{
                    execute(response);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            else if(telegramMessage.equals("/health_probe")){
                String message= SearchForSensorById("1002.0");
                SendMessage response=new SendMessage();
                response.setChatId(update.getMessage().getChatId().toString());
                response.setText(message);
                try{
                    execute(response);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            else if(telegramMessage.equals("/probe_messages_second_probe")){
                String message= SearchForSensorById("1002.12");
                SendMessage response=new SendMessage();
                response.setChatId(update.getMessage().getChatId().toString());
                response.setText(message);
                try{
                    execute(response);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            /*
             * Ubuntu Device Query
             */
            else if(telegramMessage.equals("/ping_time_ubuntu")){
                String message= SearchForSensorById("2185.0");
                SendMessage response=new SendMessage();
                response.setChatId(update.getMessage().getChatId().toString());
                response.setText(message);
                try{
                    execute(response);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            else if(telegramMessage.equals("/loading_time_ubuntu")){
                String message= SearchForSensorById("2186.0");
                SendMessage response=new SendMessage();
                response.setChatId(update.getMessage().getChatId().toString());
                response.setText(message);
                try{
                    execute(response);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            else if(telegramMessage.equals("/system_uptime_ubuntu")){
                String message= SearchForSensorById("2192.0");
                SendMessage response=new SendMessage();
                response.setChatId(update.getMessage().getChatId().toString());
                response.setText(message);
                try{
                    execute(response);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            else if(telegramMessage.equals("/closed_ports_ubuntu")){
                String message= SearchForSensorById("2196.2");
                SendMessage response=new SendMessage();
                response.setChatId(update.getMessage().getChatId().toString());
                response.setText(message);
                try{
                    execute(response);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            else if(ResponseMessage==""){
            if(telegramMessage.charAt(0)!='/'){
                String message="this telegramMessage isn't recognized";
                SendMessage response=new SendMessage();
                response.setChatId(update.getMessage().getChatId().toString());
                response.setText(message);
                try{
                    execute(response);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }


        // TODO
    }

    @Override
    public String getBotUsername() {
        // TODO
        return "ahmgh567Bot";
    }

    @Override
    public String getBotToken() {
        // TODO
        return "";//here your token
    }
}
