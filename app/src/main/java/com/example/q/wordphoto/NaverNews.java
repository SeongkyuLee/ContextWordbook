package com.example.q.wordphoto;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class NaverNews {
    ArrayList<News> mNewsList;
    public ArrayList<News> getNews(String query, int displayNum) throws Exception {
        String clientID="uxpvELfjZuuCHSetOeLU";
        String clientSecret = "PQNx7Pq2wi";

        URL url = new URL("https://openapi.naver.com/v1/search/news.xml?query=" + query + "&display=" + displayNum);

        //openConnection 해당 요청에 대해서 쓸 수 있는 connection 객체
        HttpURLConnection con= (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("X-Naver-Client-ID", clientID);
        con.setRequestProperty("X-Naver-Client-Secret", clientSecret);

        int responseCode = con.getResponseCode();
        BufferedReader br;

        if(responseCode==200) {
            br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        } else {
            br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
        }

        String inputLine = null;
        StringBuffer response = new StringBuffer();
        String data;
        while((inputLine = br.readLine())!=null) {
            response.append(inputLine);
        }
        br.close();

        data = response.toString();
        ArrayList<News> newsList = parseXML(data);

        return newsList;
    }

    public ArrayList<News> parseXML(String data) throws Exception {
        ArrayList<News> newsList = null;
        //응답받은 xml문서 xml문서로부터 내가 원하는 값 탐색하기(xml 파싱)
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        //연결하는거 담고
        XmlPullParser parser = factory.newPullParser();
        parser.setInput(new StringReader(data));
        int eventType= parser.getEventType();
        News tempNews = null;

        while(eventType != XmlPullParser.END_DOCUMENT){
            switch(eventType){
                case XmlPullParser.END_DOCUMENT://문서의 끝
                    break;
                case XmlPullParser.START_DOCUMENT:
                    newsList = new ArrayList<News>();
                    break;
                case XmlPullParser.END_TAG:{
                    String tag = parser.getName();
                    if(tag.equals("item")){
                        newsList.add(tempNews);
                        tempNews = null;
                    }
                }

                case XmlPullParser.START_TAG:{ //무조건 시작하면 만남
                    String tag = parser.getName();
                    switch(tag){
                        case "item": //item가 열렸다는것은 새로운 책이 나온다는것
                            tempNews = new News();
                            break;
                        case "title":
                            if(tempNews!=null)
                                tempNews.setTitle(parser.nextText());
                            break;
                        case "link":
                            if(tempNews!=null)
                                tempNews.setLink(parser.nextText());
                            break;
                        case "description":
                            if(tempNews!=null) {
                                String tempText = parser.nextText();
                                tempText = tempText.replaceAll("&quot;", "'");
                                tempNews.setDescription(tempText);
                            }
                            break;
                    }
                    break;
                }
            }
            eventType = parser.next();
        }
        return newsList;
    }
}
