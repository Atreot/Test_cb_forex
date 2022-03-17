package com.example.test_cb_forex;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private static final String JSON_URL = "https://www.cbr-xml-daily.ru/daily_json.js";
    private SwipeRefreshLayout mSwipeRefreshLayout;//компонент для ручного обновления списка
    private Timer periodicUpdatesTimer;//компонент для автоматического обновления списка
    ListView listView;//список
    ProgressBar progressBar;//ProgressBar для отображения загрузки данных
    Currency_conversion_dialog ConvertDialog;//диалог для коныертации валюты

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //region определение и настройка всех компонентов
        progressBar =findViewById(R.id.progress_bar);

        mSwipeRefreshLayout=findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.CYAN);

        periodicUpdatesTimer = new Timer();
        periodicUpdatesTimer.schedule(new PeriodicUpdatesTimer(),60000,60000);

        listView = findViewById(R.id.list_view);
        //endregion
        loadJSONFromURL(JSON_URL);//загрузка и отображение данных из JSON
        setListViewClickListner();//обработчик событий в listView


    }

    private void  loadJSONFromURL(String url){
        progressBar.setVisibility(ListView.VISIBLE);
        //region создание запроса, при получении данных заполняем listView контентом
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener< String>(){
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.INVISIBLE);
                        try {
                            JSONObject object = new JSONObject(response).getJSONObject("Valute");
                            ArrayList< JSONObject> listItems = getArrayListFromJSONArray(object);
                            ListAdapter adapter = new ListViewAdapter(getApplicationContext(),R.layout.div,R.id.textViewName,listItems);
                            listView.setAdapter(adapter);
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
        //endregion
        //region отправка запроса
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        //endregion
    }

    private ArrayList< JSONObject> getArrayListFromJSONArray(JSONObject jsonArray){
        //вспомогательный метод создающий нужный массив из полученных данных
        ArrayList< JSONObject> aList = new ArrayList< JSONObject>();
        int i=0;
        try {
            if(jsonArray!= null){
                Iterator<String> it=jsonArray.keys();
                while (it.hasNext()){
                    aList.add(jsonArray.getJSONObject(it.next()));

                }
            }
        }catch (JSONException js){
            js.printStackTrace();
        }
        return aList;
    }

    private void setListViewClickListner() {
        //метод для создания обработчика событий в listView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                try {
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    Fragment prev = getFragmentManager().findFragmentByTag("dialog");
                    if (prev != null) {
                        ft.remove(prev);
                    }
                    ft.addToBackStack(null);
                    JSONObject obj=((JSONObject) parent.getItemAtPosition(position));
                    ConvertDialog=ConvertDialog.newInstance(obj.getString("Nominal")+" "+obj.getString("Name"),obj.getString("CharCode"),obj.getDouble("Value"));
                    ConvertDialog.show(ft,"Currency_conversion_dialog");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        //метод вызывается, когда пользователь хочет обновить контент
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Отменяем анимацию обновления
                loadJSONFromURL(JSON_URL);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 700);
    }
    class PeriodicUpdatesTimer extends TimerTask {
        //метод периодического обновления контента
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(true);
                    onRefresh();
                }
            });
        }
    }

}