package com.example.test_cb_forex;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
/*
ListViewAdapter для создания полей из выгруженных данных JSON
*/

public class ListViewAdapter extends ArrayAdapter<JSONObject> {
    int listLayout;
    ArrayList<JSONObject> usersList;
    Context context;


    public ListViewAdapter(@NonNull Context context, int listLayout, int textViewResourceId, ArrayList<JSONObject> usersList) {
        super(context, listLayout, textViewResourceId, usersList);
        this.context=context;
        this.listLayout=listLayout;
        this.usersList=usersList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        //region получение доступа ко всем необходимым компонентам
        View listViewItem=inflater.inflate(listLayout,null,false);
        TextView name=listViewItem.findViewById(R.id.textViewName);
        TextView Value=listViewItem.findViewById(R.id.textViewValue);
        TextView Previous=listViewItem.findViewById(R.id.textViewPrevious);
        //endregion

        try {
            //region наполнение данными компонентов
            name.setText(usersList.get(position).getInt("Nominal")+" "+usersList.get(position).getString("Name"));
            Value.setText(usersList.get(position).getString("Value"));
            Previous.setText(usersList.get(position).getString("Previous"));
            //endregion
        }catch (JSONException je){  je.printStackTrace();}
        return listViewItem;
    }

}
