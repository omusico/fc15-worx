package hr.heisenbug.worxapp.helpers;

/**
 * Created by mathabaws on 5/29/15.
 */
public class AuthTokenGenerator implements Runnable {

    //thread cycle in minutes (1 min = 60 000 miliseconds)
    public static final int THREAD_CYCLE = 10;

    @Override
    public void run() {
        String authToken = "";
        while (true) {

            AutodeskApiHelpers aah = new AutodeskApiHelpers();

            //if there are no consumer key or secret don't make API call
            if (StaticData.getConsumerKey() == "" || StaticData.getConsumerSecret() == "") {
                System.err.println("Consumer key and Consumer secret are not set!");
            } else {
                //make API call
                authToken = aah.authenticateApi(StaticData.getConsumerKey(), StaticData.getConsumerSecret());
                System.out.println("New Auth token generated: "+authToken);
                //set auth token
                StaticData.setAuthorizationToken(authToken);
            }

            //sleep for x minutes.
            try {
                Thread.sleep(THREAD_CYCLE * 60000);
            } catch (InterruptedException e) {
                System.out.println("AuthTokenGenerator service interrupted!");
                e.printStackTrace();
            }
        }
    }
}
