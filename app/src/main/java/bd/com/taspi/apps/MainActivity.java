package bd.com.taspi.apps;

import static bd.com.taspi.apps.CustomTools.SERVER_URL;
import static bd.com.taspi.apps.CustomTools.log;
import static bd.com.taspi.apps.StaticData.getMyAppServices;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private GridView gridView;
    private ArrayList<Map<String, String>> serviceList;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        log(SERVER_URL);

        activity = this;


        gridView = findViewById(R.id.gridView);

        // Initialize the list
        serviceList = getMyAppServices();

        // Set up the adapter
        ServiceAdapter adapter = new ServiceAdapter(serviceList);
        gridView.setAdapter(adapter);

        activity.findViewById(R.id.titleBar).setOnLongClickListener(v -> {
            activity.startActivity(new Intent(activity, AdminPanel.class));
            return true;
        });

    }





    public class ServiceAdapter extends BaseAdapter {
        private final ArrayList<Map<String, String>> serviceList;

        public ServiceAdapter(ArrayList<Map<String, String>> serviceList) {
            this.serviceList = serviceList;
        }

        @Override
        public int getCount() {
            return serviceList.size();
        }

        @Override
        public Object getItem(int position) {
            return serviceList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_services, parent, false);
            }

            ImageView icon = convertView.findViewById(R.id.icon);
            TextView title = convertView.findViewById(R.id.title);

            // Get the service item from the list
            Map<String, String> service = serviceList.get(position);

            // Set the title
            title.setText(service.get("title"));

            // Set the icon dynamically based on the icon name
            String iconName = service.get("icon");
            int iconResId = getResources().getIdentifier(iconName, "drawable", getPackageName());
            icon.setImageResource(iconResId);

            convertView.setOnClickListener(v -> {
                Intent intent = null;
                if(Objects.equals(service.get("id"), "0")) {
                    intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:999"));
                } else if (Objects.equals(service.get("id"), "2")) {
                    intent = new Intent(activity, RequestForListing.class);
                }else{
                    intent = new Intent(activity, NumberListOfService.class);
                    intent.putExtra("serviceType", service.get("id"));
                    intent.putExtra("serviceTitle", service.get("title"));
                }


                activity.startActivity(intent);
            });

            return convertView;
        }
    }
}
