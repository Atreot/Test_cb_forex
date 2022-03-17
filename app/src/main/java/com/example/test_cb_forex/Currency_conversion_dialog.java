package com.example.test_cb_forex;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import androidx.fragment.app.FragmentManager;

/*
        Класс диалога для конвертации валюты
 */

public class Currency_conversion_dialog extends DialogFragment {
    String Name, CharCode;
    Double Value;
    EditText editTextRUB;
    EditText editTextCurrency;
    String auxiliaryStr;

    static Currency_conversion_dialog newInstance(String name,String charCode,Double value) {
        //метод для наполнения данными диалог
        Currency_conversion_dialog f = new Currency_conversion_dialog();
        Bundle args = new Bundle();
        //region наполнение данными Bundle
        args.putDouble("value", value);
        args.putString("name",name);
        args.putString("currency",charCode);
        f.setArguments(args);
        //endregion

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //region получение данных из Bundle
        Name=getArguments().getString("name");
        CharCode=getArguments().getString("currency");
        Value=getArguments().getDouble("value");
        //endregion
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle("Title!");
        View v = inflater.inflate(R.layout.currency_conversion_dialog, null);
        ((TextView)v.findViewById(R.id.textViewName)).setText(Name);
        ((TextView)v.findViewById(R.id.textViewCurrency)).setText(CharCode);
        //region получение доступа ко всем необходимым компонентам
        editTextCurrency=v.findViewById(R.id.editTextCurrency);
        editTextRUB=v.findViewById(R.id.editTextRUB);
        //endregion
        editTextRUB.requestFocus();//при появлении диалога курсор ввода устанавливается на 1 textView
        //при появлении диалога сразу появляется клавиатура для ввода
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        editTextRUB.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //region манипуляции с текстовыми полями для editTextRUB
                try {
                    if(editTextRUB.hasFocus()) {
                        if(editTextRUB.getText().length()!=0) {
                            if(editTextRUB.getText().length()==1)auxiliaryStr="0";else
                            auxiliaryStr = String.format("%.2f", Double.parseDouble(editTextRUB.getText().toString().replace(',', '.')) / Value);
                        }else{
                            auxiliaryStr=null;
                        }
                        editTextCurrency.setText(auxiliaryStr);
                    }
                }catch (Exception e){e.printStackTrace();}
                //endregion
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
        });
        editTextCurrency.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //region манипуляции с текстовыми полями для editTextRUB
                try {
                    if(editTextCurrency.hasFocus()) {
                        if(editTextCurrency.getText().length()!=0){
                            if(editTextCurrency.getText().length()==1)auxiliaryStr="0";else
                            auxiliaryStr=String.format("%.2f", Double.parseDouble(editTextCurrency.getText().toString().replace(',', '.')) * Value);
                        }else{
                            auxiliaryStr=null;
                        }
                        editTextRUB.setText(auxiliaryStr);
                    }
                }catch (Exception e){e.printStackTrace();}
                //endregion
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
        });

        return v;
    }

    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }

    public void show(FragmentManager supportFragmentManager, String currency_conversion_dialog) {

    }
}
