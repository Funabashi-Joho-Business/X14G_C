package jp.ac.chiba_fjb.x14b_c.naroreader.data;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class Json{

    public static <T> T send(String adress,Object obj, Class<T> valueType){
        HttpURLConnection connection = null;
        StringBuilder sb = new StringBuilder();

        try {
            //JSON用オブジェクトの作成
            ObjectMapper mapper = new ObjectMapper();
            //URLの設定
            URL url = new URL(adress);
            connection = (HttpURLConnection)url.openConnection();
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Content-Type", String.format("text/plain"));
            connection.setRequestProperty("Accept-Encoding", "gzip,deflate,sdch");
            connection.setDoOutput(true);
            //オブジェクトをJSONデータに変換して出力
            OutputStream os = connection.getOutputStream();
            if(obj != null) {
                byte[] data = mapper.writeValueAsBytes(obj);
                os.write(data);
            }
            os.close();
            final Map<String,List<String>> headers = connection.getHeaderFields();

            InputStreamReader is;
            if(headers.get("Content-Type")!=null && headers.get("Content-Type").get(0).equals("application/x-gzip"))
                is = new InputStreamReader(new GZIPInputStream(connection.getInputStream()),"UTF-8");
            else
                is = new InputStreamReader(connection.getInputStream(),"UTF-8");
            BufferedReader br    = new BufferedReader(is);



            //    １行ずつ書き出す
            String line;
            while((line=br.readLine()) != null)
            {
                sb.append(line);
            }
            is.close();
            br.close();
            return mapper.readValue(sb.toString(),valueType);
        }catch (Exception e){
            e.printStackTrace();
            //エラー応答の内容を返す
            System.out.println(sb.toString());
        }
        finally {
            if(connection != null)
                connection.disconnect();
        }
        return null;
    }
}