package space.manokhin.myweather;

import android.app.Application;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class MyCustomApplication: Application() {
    val executorService: ExecutorService = Executors.newFixedThreadPool(4)
}
