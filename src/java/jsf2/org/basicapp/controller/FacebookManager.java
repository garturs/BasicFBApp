/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf2.org.basicapp.controller;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.types.Post;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

/**
 *
 * @author Arturs Gusjko
 */
@ManagedBean( name = "facebookManager" )
@SessionScoped
public class FacebookManager
{

    private static final String API_KEY = "my_api_key"; // api key
    private static final String SECRET_KEY = "my_secret_key";

    /*
     * @ManagedProperty(value="#{facebookTokenManager}") private
     * FacebookTokenManager fbtokens;
     */
    private FacebookClient fbclient;

    /**
     * Creates a new instance of FacebookManager
     */
    public FacebookManager()
    {
    }
    private String access_token;

    @PostConstruct
    public void init()
    {

        HttpServletRequest req = (HttpServletRequest)FacesContext.
                getCurrentInstance().
                getExternalContext().getRequest();

        /*
         * String error = req.getParameter("error_reason"); if(error != null) {
         * try { // you may want to pass this error to // the error redirect url
         * String error_desc = req.getParameter("error_description");
         * ((HttpServletResponse)FacesContext.getCurrentInstance().
         * getExternalContext().
         * getResponse()).sendRedirect("http://basicfbapp.jelastic.com/"); }
         * catch (Exception e) { } }
         */

        String code = req.getParameter("code");
        if(code != null) {
            access_token = retrieveToken(code);
            if(access_token != null) {
                fbclient = new DefaultFacebookClient(getAccessToken());
            }

            // process return value
        }
        else {
            // Redirect or tell the user about the error
            access_token = "nothing";
        }
    }

    private String retrieveToken(String code)
    {

        HttpClient client = new DefaultHttpClient();
        HttpPost post =
                 new HttpPost(
                "https://graph.facebook.com/oauth/access_token");

        try {

            String[][] parameters = {
                {"client_id", API_KEY},
                {"client_secret", SECRET_KEY},
                {"redirect_uri",
                 "http://basicfbapp.jelastic.com/faces/messages.xhtml"},
                {"code", code}
            };


            List<NameValuePair> nameValuePairs =
                                new ArrayList<NameValuePair>(1);

            for(int i = 0; i < parameters.length; i++) {
                nameValuePairs.add(new BasicNameValuePair(parameters[i][0],
                                                          parameters[i][1]));
            }

            post.setEntity(
                    new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse resp = client.execute(post);
            BufferedReader rd =
                           new BufferedReader(new InputStreamReader(
                    resp.getEntity().getContent()));

            String message = "";
            String lineData;
            while((lineData = rd.readLine()) != null) {
                message += lineData;
            }

            String token = null;

            // Add more safety traps
            String[] params = message.split("&");
            if(params != null) {
                for(int i = 0; i < params.length; i++) {
                    if(params[i].contains("access_token")) {
                        String[] B = params[i].split("=");
                        if(B != null) {
                            token = B[1];
                        }
                        break;
                    }
                }
            }
            else {
                // Let the user know about the error.
                //return false;
            }

            return token;

            //return true;

        }
        catch (Exception e) {
            //return false;
            return null;
        }

    }

    public String getAllPosts()
    {
        Connection<Post> myFeed = fbclient.fetchConnection("me/feed", Post.class);
        StringBuilder st = new StringBuilder();
        int i = 0;
        for(Post post : myFeed.getData()) {
            st.append("<p>From: ");
            st.append(post.getFrom().getName());
            st.append("</p>");
            st.append("<p>About: ");
            st.append(post.getCaption());
            st.append("</p>");
            if(null != post.getPicture()) {
                st.append("<img src=\"");
                st.append(post.getPicture());
                st.append("\" />");
            }
            st.append("<p>Message:</p>");
            st.append("<p>");
            st.append(post.getMessage());
            st.append("</p><br/>");
            if(i++ >= 10) {
                break;
            }
        }
        st.append(
                "<div id=\"fb-root\"></div><script>(function(d, s, id) { var js, fjs = d.getElementsByTagName(s)[0];if (d.getElementById(id)) return;js = d.createElement(s); js.id = id; js.src = \"//connect.facebook.net/en_GB/all.js#xfbml=1&appId=APP_ID\"; fjs.parentNode.insertBefore(js, fjs);}(document, 'script', 'facebook-jssdk'));</script>");
        st.append("<div class=\"fb-live-stream\" data-event-app-id=\"312047405509648\" data-width=\"400\" data-height=\"500\" data-always-post-to-friends=\"false\"></div>");
        return st.toString();
    }

    public String getAccessToken()
    {
        return access_token;
    }
}
