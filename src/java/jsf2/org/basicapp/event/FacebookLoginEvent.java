package jsf2.org.basicapp.event;


import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Arturs Gusjko
 * @version 1.0
 */
@ManagedBean(name="facebookLoginEvent")
@ViewScoped
public class FacebookLoginEvent {
        
    public void logIn(ActionEvent event)
    {
        String fbUrl = "https://www.facebook.com/dialog/oauth?"
                       + "client_id=" + "my_api_key"
                       + "&redirect_uri=" + "http://basicfbapp.jelastic.com/faces/messages.xhtml"
                       + "&scope=read_mailbox,read_stream"
                       + "&response_type=code";
        HttpServletResponse response =
                            (HttpServletResponse)FacesContext.getCurrentInstance().
                getExternalContext().getResponse();
        try {
            response.sendRedirect(fbUrl);
        }
        catch (Exception ex) {
        }
    }
}
