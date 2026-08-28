package com.example.techfix.features.services.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.techfix.features.services.data.Service;
import com.example.techfix.features.services.data.ServiceRepository;
import java.util.List;

// Manages data fetching and logic for the Services screen
public class ServicesViewModel extends AndroidViewModel {
    private final MutableLiveData<List<Service>> services = new MutableLiveData<>();

    public ServicesViewModel(@NonNull Application application) {
        super(application);
    }

    // Fetches the services list from the repository in a background thread
    public void loadServices() {
        new Thread(() -> {
            List<Service> result = ServiceRepository.getInstance(getApplication()).getAllServices();
            // Use postValue to update the LiveData from a background thread
            services.postValue(result);
        }).start();
    }

    // Provides the services list as observable data to the Activity
    public LiveData<List<Service>> getServices() {
        return services;
    }
}
