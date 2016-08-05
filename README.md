# This repository demostrates AsyncTask

### There are also other ways of doing threads:

 * Java Threads
   * AsyncTask
   * Handler

#### Java Threads:

- AsyncTask and Handler are written in Java (internally they use a Thread)
- Network operations which involve moderate to large amounts of data (either uploading or downloading) 
- High-CPU tasks which need to be run in the background
- Any task where you want to control the CPU usage relative to the GUI thread 

#### AsyncTask

- Caller thread is a UI Thread. (The AsyncTask instance must be created and invoked in the UI thread.
The methods overridden in the AsyncTask class should never be called. They're called automatically
AsyncTask can be called only once. Executing it again will throw an exception)
- For simple network operations which do not require downloading a lot of data
- For disk-bound tasks that might take more than a few milliseconds
- No need to manipulate handlers, hence easier in this sense

Android AsyncTask is an abstract class provided by Android which gives us the liberty to perform heavy tasks in the background and keep the UI thread light thus making the application more responsive.

Android application runs on a single thread when launched. Due to this single thread model tasks that take longer time to fetch the response can make the application non-responsive. To avoid this we use android AsyncTask to perform the heavy tasks in background on a dedicated thread and passing the results back to the UI thread. Hence use of AsyncTask in android application keeps the UI thread responsive at all times.


The basic methods used in an android AsyncTask class are defined below :

+ doInBackground() : This method contains the code which needs to be executed in background. In this method we can send results multiple times to the UI thread by publishProgress() method. To notify that the background processing has been completed we just need to use the return statements

+ onPreExecute() : This method contains the code which is executed before the background processing starts

+ onPostExecute() : This method is called after doInBackground method completes processing. Result from doInBackground is passed to this method

+ onProgressUpdate() : This method receives progress updates from doInBackground method, which is published via publishProgress method, and this method can use this progress update to update the UI thread

The three generic types used in an android AsyncTask class are given below :

+ Params : The type of the parameters sent to the task upon execution

+ Progress : The type of the progress units published during the background computation

+ Result : The type of the result of the background computation

#### Handler 

- More transparent, hence, more control

```java
public class ThreadExampleActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread_example);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            TextView myTextView = (TextView)findViewById(R.id.myTextView);
            myTextView.setText("Button Pressed");
        }
    };

	public void buttonClick(View view) {
		Runnable runnable = new Runnable() {
			public void run() {
			long endTime = System.currentTimeMillis() + 20*1000;
			while (System.currentTimeMillis() < endTime) {
					synchronized (this) {
						try {
							wait(endTime - System.currentTimeMillis());
						} catch (Exception e) {}
					}
				}
				handler.sendEmptyMessage(0);
			}
		};
		Thread mythread = new Thread(runnable);
		mythread.start();

	}
}
```
