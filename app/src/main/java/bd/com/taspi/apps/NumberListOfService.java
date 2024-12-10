package bd.com.taspi.apps;

import static bd.com.taspi.apps.CustomTools.alert;
import static bd.com.taspi.apps.CustomTools.log;
import static bd.com.taspi.apps.CustomTools.setClipboard;
import static bd.com.taspi.apps.CustomTools.toast;
import static bd.com.taspi.apps.StaticData.TAG;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class NumberListOfService extends AppCompatActivity {
    public Activity activity;

    private Spinner spinnerCity, spinnerSpecialization; // Using one spinner for both city and specialization
    private EditText etPersonName, etOrganizationName, etServiceArea;
    private RecyclerView recyclerViewResults;
    private TextView tvResults;
    ProgressBar progressBar;

    private String serviceType = "POLICE"; // Default service type

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_list_of_service); // Replace with your actual layout
        activity = this;

        // Initialize views
        spinnerCity = findViewById(R.id.spinner_city);
        spinnerSpecialization = findViewById(R.id.spinner_specialization);
        etPersonName = findViewById(R.id.et_person_name);
        etOrganizationName = findViewById(R.id.et_organization_name);
        etServiceArea = findViewById(R.id.et_service_area);
        progressBar = findViewById(R.id.progressBar);
        // Initialize the RecyclerView
        recyclerViewResults = findViewById(R.id.recycler_view_results);
        recyclerViewResults.setLayoutManager(new LinearLayoutManager(this));
        tvResults = findViewById(R.id.tv_results); // Results TextView

        // Retrieve service type from extras
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("serviceType")) {
            serviceType = bundle.getString("serviceType");
        }
        if (bundle != null && bundle.containsKey("serviceTitle")) {
            String serviceTitle = bundle.getString("serviceTitle");
            ((TextView)activity.findViewById(R.id.titleBarText)).setText(serviceTitle);
        }

        // Load cities and specializations from static data
        loadCities();
        loadSpecializations(); // Load specializations which also act as designations

        // Set up listeners
        setupListeners();
    }

    private void loadCities() {
        // Use static data for cities
        String[] cities = StaticData.bangladeshDistricts;
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cities);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCity.setAdapter(cityAdapter);
    }

    private void loadSpecializations() {
        String[] specializations;

        // Set specializations based on the service type
        switch (serviceType) {
            case "DOCTORS":
                specializations = StaticData.doctorsSpecialist; // Load doctors' specializations
                break;
            case "POLICE":
                specializations = StaticData.policeDesignations; // Load police ranks (or any relevant specializations)
                break;
            case "FIRE_SERVICE":
                specializations = StaticData.fireServiceDesignations; // Load fire service specializations
                break;
            case "RAB":
                specializations = StaticData.rabDesignations; // Load hospital departments
                break;
            // Add other cases as needed based on your services
            default:
                activity.findViewById(R.id.designationID).setVisibility(View.GONE);
                etPersonName.setVisibility(View.GONE);
                specializations = new String[]{}; // If no specific specialization is required
                break;
        }

        // Set the adapter with the specializations for the selected service type
        ArrayAdapter<String> specializationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, specializations);
        specializationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSpecialization.setAdapter(specializationAdapter);
    }


    private void loadServices() {
        // Prepare data to send
        Map<String, String> formData = new HashMap<>();
        formData.put("get_service_type", serviceType);
        formData.put("service_city", spinnerCity.getSelectedItem().toString());
        formData.put("person_specialization", spinnerSpecialization.getSelectedItem() != null ?spinnerSpecialization.getSelectedItem().toString():null);

        String personName = etPersonName.getText().toString().trim();
        if (!personName.isEmpty()) {
            formData.put("person_name", personName);
        }

        String organizationName = etOrganizationName.getText().toString().trim();
        if (!organizationName.isEmpty()) {
            formData.put("organization_name", organizationName);
        }

        String serviceArea = etServiceArea.getText().toString().trim();
        if (!serviceArea.isEmpty()) {
            formData.put("service_area", serviceArea);
        }

        // Call the Internet3 class to handle the network request
        progressBar.setVisibility(View.VISIBLE);
        new Internet3(this, CustomTools.url("es.php"), formData, (code, result) -> {
            if (code == 200) {
//                log(result);
//                log(formData);
                // Handle the result to update the ListView
                progressBar.setVisibility(View.GONE);
                updateListView(result);
            } else {
                Log.e(TAG, "Failed to fetch data. Error code: " + code);
            }
        }).connect();
    }

    private void updateListView(JSONObject result) {
        try {
            JSONArray output = result.getJSONArray("output");

            // Clear previous results
            ArrayList<Map<String, String>> resultsList = new ArrayList<>();
            for (int i = 0; i < output.length(); i++) {
                JSONObject serviceData = output.getJSONObject(i);
                Map<String, String> map = new HashMap<>();

                // Extract data from JSON object
                map.put("service_type", serviceData.optString("service_type", ""));
                map.put("person_name", serviceData.optString("person_name", ""));
                map.put("organization_name", serviceData.optString("organization_name", ""));
                map.put("service_city", serviceData.optString("service_city", ""));
                map.put("service_area", serviceData.optString("service_area", ""));
                map.put("person_specialization", serviceData.optString("person_specialization", ""));
                map.put("phone_number", serviceData.optString("phone_number", ""));
                map.put("time", serviceData.optString("time", ""));

                // Add to the list only if not completely empty
                resultsList.add(map);
            }

            // Use the RecyclerView adapter to show the list
            CustomRecyclerAdapter adapter = new CustomRecyclerAdapter(resultsList);
            recyclerViewResults.setAdapter(adapter);

            tvResults.setVisibility(resultsList.isEmpty() ? View.GONE : View.VISIBLE);

        } catch (JSONException e) {
            log(e.getMessage());
        }
    }






    private void setupListeners() {
        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadServices(); // Reload services when city changes
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerSpecialization.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadServices(); // Reload services when specialization changes
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        etPersonName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadServices(); // Reload services when text changes
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        etOrganizationName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadServices(); // Reload services when text changes
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        etServiceArea.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadServices(); // Reload services when text changes
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private class CustomRecyclerAdapter extends RecyclerView.Adapter<CustomRecyclerAdapter.ViewHolder> {

        private ArrayList<Map<String, String>> servicesList;

        public CustomRecyclerAdapter(ArrayList<Map<String, String>> list) {
            this.servicesList = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_service, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Map<String, String> serviceData = servicesList.get(position);

            if(!Objects.equals(serviceData.get("person_name"), "")){
                holder.tvPersonName.setText(serviceData.get("person_name"));
                holder.linearLayout_person_name.setVisibility(View.VISIBLE);
            }else{
                holder.linearLayout_person_name.setVisibility(View.GONE);
            }
            if(!Objects.equals(serviceData.get("organization_name"), "")){
                holder.tvOrganizationName.setText(serviceData.get("organization_name"));
                holder.linearLayout_organization_name.setVisibility(View.VISIBLE);
            }else{
                holder.linearLayout_organization_name.setVisibility(View.GONE);
            }
            if(!Objects.equals(serviceData.get("service_city"), "") && !Objects.equals(serviceData.get("service_city"), "All")){
                holder.tvServiceCity.setText(serviceData.get("service_city"));
                holder.linearLayout_service_city.setVisibility(View.VISIBLE);
            }else{
                holder.linearLayout_service_city.setVisibility(View.GONE);
            }
            if(!Objects.equals(serviceData.get("service_area"), "")){
                holder.tvServiceArea.setText(serviceData.get("service_area"));
                holder.linearLayout_service_area.setVisibility(View.VISIBLE);
            }else{
                holder.linearLayout_service_area.setVisibility(View.GONE);
            }
            if(!Objects.equals(serviceData.get("person_specialization"), "") && !Objects.equals(serviceData.get("person_specialization"), "All") ){
                holder.tvPersonSpecialization.setText(serviceData.get("person_specialization"));
                holder.linearLayout_person_specialization.setVisibility(View.VISIBLE);
            }else{
                holder.linearLayout_person_specialization.setVisibility(View.GONE);
            }
            if(!Objects.equals(serviceData.get("phone_number"), "")){
                holder.tvPhoneNumber.setText(serviceData.get("phone_number"));
                holder.linearLayout_phone_number.setVisibility(View.VISIBLE);
            }else{
                holder.linearLayout_phone_number.setVisibility(View.GONE);
            }

            holder.itemView.setOnClickListener(v -> {
                if(!Objects.equals(serviceData.get("phone_number"), "")){
                    Intent intentDial = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + serviceData.get("phone_number")));
                    activity.startActivity(intentDial);
                }else{
                    alert(activity, "Sorry", "no number found for this user");
                }
            });

            holder.itemView.setOnLongClickListener(v -> {
                setClipboard(activity, String.valueOf(serviceData.get("phone_number")));
                toast(activity, "Number Copied");
                return true;
            });



        }

        @Override
        public int getItemCount() {
            return servicesList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView tvPersonName, tvOrganizationName, tvServiceCity, tvServiceArea, tvPersonSpecialization, tvPhoneNumber;
            public LinearLayout linearLayout_person_name,linearLayout_organization_name ,linearLayout_service_city,linearLayout_service_area ,linearLayout_person_specialization,linearLayout_phone_number;
            public View itemView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                this.itemView = itemView;

                tvPersonName = itemView.findViewById(R.id.tv_person_name);
                tvOrganizationName = itemView.findViewById(R.id.tv_organization_name);
                tvServiceCity = itemView.findViewById(R.id.tv_service_city);
                tvServiceArea = itemView.findViewById(R.id.tv_service_area);
                tvPersonSpecialization = itemView.findViewById(R.id.tv_person_specialization);
                tvPhoneNumber = itemView.findViewById(R.id.tv_phone_number);

                linearLayout_person_name = itemView.findViewById(R.id.linearLayout_person_name);
                linearLayout_organization_name = itemView.findViewById(R.id.linearLayout_organization_name);
                linearLayout_service_city = itemView.findViewById(R.id.linearLayout_service_city);
                linearLayout_service_area = itemView.findViewById(R.id.linearLayout_service_area);
                linearLayout_person_specialization = itemView.findViewById(R.id.linearLayout_person_specialization);
                linearLayout_phone_number = itemView.findViewById(R.id.linearLayout_phone_number);
            }
        }
    }
}
