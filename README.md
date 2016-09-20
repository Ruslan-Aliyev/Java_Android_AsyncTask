# This repository demostrates AsyncTask

### There are also other ways of doing threads:

 * Java Threads
   * AsyncTask
   * Handler

#### Java Threads:

- AsyncTask and Handler internally they use Thread
- Network operations which involve moderate to large amounts of data (either uploading or downloading) 
- High-CPU tasks which need to be run in the background
- Any task where you want to control the CPU usage relative to the GUI thread 
- Cant touch IU
- Dont destroy() nor stop() 
- Do interrupt() or join() 

Providing a new class that extends Thread and overriding its run() method:

```java
protected void someFunction(){
	Thread t = new Thread(){
		public void run(){
			// ...
		}
	};
	t.start();
}
```

Providing a new Thread instance with a Runnable object during its creation:

```java
private static class RunnableObject implements Runnable{
	public void run(){
		// ...
	}
}
public static void main(String args[]) throws InterruptedException {
	Thread t = new Thread(new RunnableObject());
	t.start();
}
```

#### AsyncTask

- Caller thread is a UI Thread. (The AsyncTask instance must be created and invoked in the UI thread.
The methods overridden in the AsyncTask class should never be called. They're called automatically
AsyncTask can be called only once. Executing it again will throw an exception)
- Created on the UI thread and can be executed only once. 
- Run on a background thread and result is published on the UI thread
- For simple network operations which do not require downloading a lot of data
- For disk-bound tasks 

AsyncTask is designed to be a helper class around Thread and Handler and does not constitute a generic threading framework. AsyncTasks should ideally be used for short operations (a few seconds at the most.) If you need to keep threads running for long periods of time, it is highly recommended you use the various APIs provided by the java.util.concurrent package such as Executor, ThreadPoolExecutor and FutureTask. 

Android AsyncTask is an abstract class provided by Android which gives us the liberty to perform heavy tasks in the background and keep the UI thread light thus making the application more responsive.

Android application runs on a single thread when launched. Due to this single thread model tasks that take longer time to fetch the response can make the application non-responsive. To avoid this we use android AsyncTask to perform the heavy tasks in background on a dedicated thread and passing the results back to the UI thread. Hence use of AsyncTask in android application keeps the UI thread responsive at all times.

Extend AsyncTask<Void, Void, Void> . The three types used by an asynchronous task are the following:

– Params, the type of the parameters sent to the task upon execution 

– Progress, the type of the progress units published during the background computation 

– Result, the type of the result of the background computation 

+ onPreExecute() : This method contains the code which is executed before the background processing starts. Invoked on the UI thread immediately after the task is executed.

+ doInBackground(Param...) : This method contains the code which needs to be executed in background. In this method we can send results multiple times to the UI thread by publishProgress() method. To notify that the background processing has been completed we just need to use the return statements. Invoked on the background thread immediately after onPreExecute() finishes executing 

+ onPostExecute(Result) : This method is called after doInBackground method completes processing. Result from doInBackground is passed to this method. Invoked on the UI thread after the background computation finishes

+ onProgressUpdate(Progress...) : This method receives progress updates from doInBackground method, which is published via publishProgress method, and this method can use this progress update to update the UI thread. Invoked on the UI thread after a call to publishProgress(Progress...) 

```java
public class at extends AsyncTask< ArrayList<Item> , Void , ArrayList<Item> >{
	@override
	protected ArrayList<Item> doInBackground(ArrayList<Item>... params){
		return itemList;
	}
	@override
	protected void onProgressUpdate(Void... unused){
	
	}
	@override
	protected void onPostExecute(ArrayList<Item> sResponse){
		// update ui
	}
}
LoadItemList loadItemList = new LoadItemList();
loadItemList.execute();
```

#### Handler 

- Associated with a single thread and that threads message queue 
- Bound to the thread / message queue of the thread that is creating it 
- Deliver messages and runnables to that message queue 
- Execute them as they come out of the message queue

Handler For:
- To schedule messages and runnables to be executed as some point in the future
- To add an action into a queue performed on a different thread

Simple example:

```java
public Handler h = new Handler(){
	@override
	public void handleMessage(Message msg){
		// ...
	}
}
Message message = h.obtainMessage("something", "");
h.sendMessage(message);
```

Complex example:

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

### Updating UI thread from a non-UI thread

#### Handler (Best way)

##### Handler posting runnable back to UI Thread

Using java.lang.Runnable: `Handler.post(Runnable)`

```java
final Handler handler = new Handler(); // In UI Thread

new Thread(new Runnable(){ // Not UI Thread
    @Override
    public void run() {
            handler.post(new Runnable() { // Posting a runnable back to UI Thread
                @Override
                public void run() {
                    doSomething(); // Posting a runnable back to UI Thread
                }
            });
    }
}).start();
```

Use Handler.postRunnable when you want to execute some code on the UI Thread without having to know anything about your Handler object. It makes sense in many cases where arbitrary code needs to be executed on the UI Thread.

##### Handler sending message back to UI Thread

Using android.os.Message: `Handler.sendMessage(Message)` / `Handler.handleMessage(Message)`

```java
final Handler handler = new Handler(){ // In UI Thread
  @Override
  public void handleMessage(Message msg) {
    if(msg.what==CONSTANT_CODE){
      images.get(msg.arg1).setImageBitmap((Bitmap) msg.obj); // doSomethingWithTheMessage
    }
    super.handleMessage(msg);
  }
};

new Thread(new Runnable(){ // Not UI Thread
    @Override
    public void run() {
	//if(dataArrives){
	    Message msg = handler.obtainMessage();
	    msg.what = CONSTANT_CODE;
	    msg.obj = bitmap;
	    msg.arg1 = index;
	    handler.sendMessage(msg); // Send message back to UI Thread
	//}
    }
}).start();
```

Initialize your non-UI thread,  passing it a Handler object. 
When data arrives use the handler to send a message to the UI thread. 
In the UI thread, when the message from the non-UI thread comes, just update the Views.

Messages can be reused, so it results in fewer objects created and less GC. You also end up with fewer classes and anonymous types.

One big advantage is that a class sending a Message to a Handler doesn't need to know anything about the implementation of that Message. That can aid in encapsulation depending on where it's used.

In some cases you want to organise what is being sent to the UI Thread and have specific functions you want to execute that way you can use sendMessage.

#### Activity.runOnUiThread(Runnable)

runOnUiThread uses Handler. runOnUiThread() posts the Runnable to a Handler if the current thread is not the UI thread. If it is the UI thread, the runnable is executed synchronously - this is not always desirable.

```java
new Thread() {  
    public void run() {  
        //background task  

        runOnUiThread(new Runnable() {  
            public void run() {  
                //UI callback  
            }  
        });  
    }  
}.start();
```

Activity.runOnUiThread() is a special case of more generic Handlers. With Handler you can create your own event query within your own thread. Using Handlers instantiated with default constructor doesn't mean "code will run on UI thread" in general. By default, handlers binded to Thread from which they was instantiated from.

To create Handler that is guaranteed to bind to UI (main) thread you should create Handler object binded to Main Looper like this:

`Handler mHandler = new Handler(Looper.getMainLooper());`

Moreover, if you check the implementation of runOnuiThread() method, it is using Handler to do the things:

```java
  public final void runOnUiThread(Runnable action) {
        if (Thread.currentThread() != mUiThread) {
            mHandler.post(action);
        } else {
            action.run();
        }
    }
```

As you can see from code snippet above, Runnable action will be executed immediately, if runOnUiThread() is called from the UI thread. Otherwise, it will post it to the Handler, which will be executed at some point later.

#### AsyncTask

AsyncTask uses handler. AsyncTask#finish() that calls onPostExecute() is called from a Handler message loop.

```java
new AsyncTask<X, Void, Z>() {  
    protected Boolean doInBackground(X... params) {  
        //background task  
    }  
    protected void onPostExecute(Z res) {  
        //UI callback  
    }  
}.execute();
```
