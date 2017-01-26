package to.pns.naroencounter.Other;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.List;

import to.pns.lib.ListView;
import to.pns.lib.LogDB;
import to.pns.lib.LogService;
import to.pns.naroencounter.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LogFragment extends Fragment {

    //通知処理
    public BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch(intent.getAction()){
                case LogService.UPDATE_NAME:
                    update();
                    break;
            }
        }
    };

    public LogFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_log, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ListView listView = (ListView) getView().findViewById(R.id.listView);
        listView.addHeader("ID");
        listView.addHeader("DATE");
        listView.addHeader("MESSAGE");
        update();
    }

    void update()
    {
        ListView listView = (ListView) getView().findViewById(R.id.listView);
        listView.clear();

        LogDB db = new LogDB(getContext());
        if(db == null)
            return;
        List<LogDB.LogData> list = db.getLog();
        db.close();

        for(LogDB.LogData log : list)
        {
            SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            int index = listView.addItem(""+log.id);
            listView.setItem(index,1,df.format(log.date));
            listView.setItem(index,2,log.msg);
            listView.setItemGravity(index,0, Gravity.LEFT);
            listView.setItemGravity(index,1, Gravity.LEFT);
            listView.setItemGravity(index,2, Gravity.LEFT);
        }
    }
}
