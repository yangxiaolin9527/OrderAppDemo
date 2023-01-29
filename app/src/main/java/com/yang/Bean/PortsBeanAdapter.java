package com.yang.Bean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yang.order_appdemo.R;
import com.yang.util.Constant;

import java.util.List;

public class PortsBeanAdapter extends ArrayAdapter<PortsBean.PortsDTO> {
    private int resourceId;

    public PortsBeanAdapter(Context context, int textViewResourceId, List<PortsBean.PortsDTO> object){
        super(context,textViewResourceId,object);
        resourceId = textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        PortsBean.PortsDTO portsDTO = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        TextView tv1 = (TextView) view.findViewById(R.id.textView6);
        TextView tv2 = (TextView) view.findViewById(R.id.textView7);
        TextView tv3 = (TextView) view.findViewById(R.id.textView9);

        tv1.setText(Integer.toString(portsDTO.getId()));
        tv2.setText(Constant.PORT_STATE_DESC.get(portsDTO.getState()));
        tv3.setText(Double.toString(portsDTO.getMaxPower()));

        return view;
    }
}
