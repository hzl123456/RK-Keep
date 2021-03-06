package rkkeep.keep.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.baidu.mapapi.search.core.PoiInfo;

import java.util.List;

import rkkeep.keep.R;

/**
 * Created by Au61 on 2016/4/29.
 */
public class PoiAdapter extends BaseAdapter {

    private List<PoiInfo> infos;


    public List<PoiInfo> getDatas() {
        return infos;
    }

    public void reflush(List<PoiInfo> infos) {
        this.infos = infos;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return infos == null ? 0 : infos.size();
    }

    @Override
    public Object getItem(int position) {
        return infos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(parent.getContext(), R.layout.item_poi, null);
            convertView.setTag(new ViewHolder(convertView));
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        holder.tvAddress.setText(infos.get(position).name);
        if (position == infos.size()) {
            holder.dvView.setVisibility(View.GONE);
        } else {
            holder.dvView.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    class ViewHolder {
        public TextView tvAddress;
        public View dvView;

        public ViewHolder(View view) {
            tvAddress = (TextView) view.findViewById(R.id.tv_address);
            dvView = (View) view.findViewById(R.id.dv_view);
        }

    }
}
