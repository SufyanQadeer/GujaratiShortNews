package com.example.gujaratishortnews;

import java.util.List;

public interface IFirestoreLoadDone {
    void onFireStoreLoadSucess(List<News> news);
    void onFireStoreLoadFailed(String message);

}
