package bd.com.taspi.apps;

import static bd.com.taspi.apps.CustomTools.log;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AdminPanel extends Activity {

    private TextView tvPersonSpecialization;
    private Spinner spinnerServiceType, spinnerDistName, spinnerPersonSpecialization;
    private EditText editTextOrganizationName, editTextPhoneNumber, editTextPersonName, editTextServiceArea;
    private Button buttonSubmit;
    private ProgressBar progressBar;
    private ListView listViewUnverified;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        activity = this;

        // Initialize UI components
        spinnerServiceType = findViewById(R.id.spinnerServiceType);
        spinnerDistName = findViewById(R.id.spinnerDistName);
        spinnerPersonSpecialization = findViewById(R.id.spinnerPersonSpecialization); // Spinner for specialization
        tvPersonSpecialization = findViewById(R.id.tvPersonSpecialization); // Spinner for specialization
        editTextOrganizationName = findViewById(R.id.editTextOrganizationName);
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        editTextPersonName = findViewById(R.id.editTextPersonName);
        editTextServiceArea = findViewById(R.id.editTextServiceArea);
        buttonSubmit = findViewById(R.id.buttonSubmit);
        progressBar = findViewById(R.id.progressBar);


        listViewUnverified = findViewById(R.id.listViewUnverified); // ListView to show unverified items
        progressBar = findViewById(R.id.progressBar);

        // Fetch unverified data from the server
        fetchUnverifiedData();

        // Populate the spinners with data
        populateServiceTypeSpinner();
        populateDistrictSpinner();

        // Set service type change listener
        spinnerServiceType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                updateSpecializationSpinner();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }
        });

        //
        final Integer[] x = {3};
        ((EditText)findViewById(R.id.et_passcode)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String pass = "123";
                if (s.length() == pass.length()) {
                    TextView tvResultPass = findViewById(R.id.tvResultPass);
                    if (String.valueOf(s).equalsIgnoreCase(pass)) {
                        InputMethodManager inputManager = (InputMethodManager)
                                getSystemService(Context.INPUT_METHOD_SERVICE);

                        inputManager.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                        findViewById(R.id.et_passcode).clearFocus();
                        findViewById(R.id.rlCheckPassCode).setVisibility(View.GONE);
                        // Set the button click listener
                        buttonSubmit.setOnClickListener(v -> submitData());
                    }else{
                        x[0]--;
                        tvResultPass.setText(x[0] + " attempt left");
                        if (x[0] == 0){
                            finishAffinity();
                            System.exit(0);
                        }
                    }
                    s.clear();
                }
            }
        });

    }

    private void fetchUnverifiedData() {
        progressBar.setVisibility(View.VISIBLE);
        // Prepare the request data
        Map<String, String> params = new HashMap<>();
        params.put("get_unverified", "1"); // Sending the request to get unverified items

        // Network request to fetch unverified data
        new Internet3(this, CustomTools.url("es.php"), params, (code, result) -> {
            progressBar.setVisibility(View.GONE);

            if (code == 200) {
                // Parse the result and update the ListView
                ArrayList<Map<String, String>> unverifiedItems = parseUnverifiedData(String.valueOf(result));
                UnverifiedAdapter adapter = new UnverifiedAdapter(this, unverifiedItems);
                listViewUnverified.setAdapter(adapter);
            } else {
                CustomTools.toast(AdminPanel.this, "Failed to fetch unverified data.");
            }
        }).connect();
    }

    private ArrayList<Map<String, String>> parseUnverifiedData(String result) {
        ArrayList<Map<String, String>> unverifiedItems = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("output");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                Map<String, String> data = new HashMap<>();
                data.put("id", item.getString("id"));
                data.put("service_type", item.getString("service_type"));
                data.put("person_name", item.getString("person_name"));
                data.put("organization_name", item.getString("organization_name"));
                data.put("service_city", item.getString("service_city"));
                data.put("service_area", item.getString("service_area"));
                data.put("person_specialization", item.getString("person_specialization"));
                data.put("phone_number", item.getString("phone_number"));

                unverifiedItems.add(data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return unverifiedItems;
    }

    private void populateServiceTypeSpinner() {
        // Retrieve the service types from StaticData
        ArrayList<Map<String, String>> services = StaticData.getMyAppServices();
        ArrayList<String> serviceTitles = new ArrayList<>();

        // Extract only the titles for the spinner
        for (Map<String, String> service : services) {
            if (!Objects.equals(service.get("id"), "0")) {
                serviceTitles.add(service.get("title"));
            }
        }

        // Create an ArrayAdapter for the service spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, serviceTitles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerServiceType.setAdapter(adapter);
    }

    private void populateDistrictSpinner() {
        // Retrieve the district names from StaticData
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, StaticData.bangladeshDistricts);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDistName.setAdapter(adapter);
    }

    private void updateSpecializationSpinner() {
        String selectedService = spinnerServiceType.getSelectedItem().toString();
        String[] specializationOptions;

        // Check which service type is selected and populate specialization accordingly
        switch (selectedService) {
            case "Doctor":
                specializationOptions = StaticData.doctorsSpecialist;
                break;
            case "Police":
                specializationOptions = StaticData.policeDesignations;
                break;
            case "RAB":
                specializationOptions = StaticData.rabDesignations;
                break;
            case "Fire Service":
                specializationOptions = StaticData.fireServiceDesignations;
                break;
            default:
                specializationOptions = new String[]{};
                break;
        }

        // If there are no specializations for the selected service type, hide the spinner
        if (specializationOptions.length == 0) {
            spinnerPersonSpecialization.setVisibility(View.GONE);
            tvPersonSpecialization.setVisibility(View.GONE);
            editTextPersonName.setVisibility(View.GONE);
        } else {
            spinnerPersonSpecialization.setVisibility(View.VISIBLE);
            tvPersonSpecialization.setVisibility(View.VISIBLE);
            editTextPersonName.setVisibility(View.VISIBLE);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, specializationOptions);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerPersonSpecialization.setAdapter(adapter);
        }
    }

    private void submitData() {
        // Show the progress bar
        progressBar.setVisibility(View.VISIBLE);

        // Prepare data to send
        ArrayList<Map<String, String>> services = StaticData.getMyAppServices();
        String serviceType = null;
        // Extract only the titles for the spinner
        for (Map<String, String> service : services) {
            if (Objects.equals(service.get("title"), spinnerServiceType.getSelectedItem().toString())) {
                serviceType = service.get("id");
                break;
            }
        }

        String districtName = spinnerDistName.getSelectedItem().toString();
        String organizationName = editTextOrganizationName.getText().toString();
        String phoneNumber = editTextPhoneNumber.getText().toString();
        String personName = editTextPersonName.getText().toString();
        String serviceArea = editTextServiceArea.getText().toString();
        String personSpecialization = "";

        // If the specialization spinner is visible, get the selected specialization
        if (spinnerPersonSpecialization.getVisibility() == View.VISIBLE) {
            personSpecialization = spinnerPersonSpecialization.getSelectedItem().toString();
        }

        // Validate required fields
        if (phoneNumber.isEmpty()) {
            CustomTools.toast(this, "Please fill in all required fields.");
            progressBar.setVisibility(View.GONE);
            return;
        }

        // Create a map to hold the form data
        Map<String, String> formData = new HashMap<>();
        formData.put("service_type", serviceType);
        formData.put("service_city", districtName);
        formData.put("organization_name", organizationName);
        formData.put("phone_number", phoneNumber);
        formData.put("person_name", personName);
        formData.put("service_area", serviceArea);
        formData.put("person_specialization", personSpecialization); // Add specialization to form data
        formData.put("add_es", "1");

        // Call the Internet3 class to handle the network request
        new Internet3(this, CustomTools.url("es.php"), formData, (code, result) -> {
            // Hide the progress bar
            progressBar.setVisibility(View.GONE);

            // Handle the result (success or error)
            if (code == 200) {
                CustomTools.toast(AdminPanel.this, "Data submitted successfully!");
                log(result);
            } else {
                CustomTools.toast(AdminPanel.this, "Failed to submit data.");
            }
        }).connect();
    }

    public class UnverifiedAdapter extends ArrayAdapter<Map<String, String>> {
        private Context context;
        private ArrayList<Map<String, String>> items;

        public UnverifiedAdapter(Context context, ArrayList<Map<String, String>> items) {
            super(context, R.layout.list_item_unverified, items);
            this.context = context;
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.list_item_unverified, parent, false);
            }

            // Get the current item
            Map<String, String> item = items.get(position);

            Button btnAccept = convertView.findViewById(R.id.btnAccept);
            Button btnReject = convertView.findViewById(R.id.btnReject);
// Get the references to the TextView elements
            TextView tvPersonName = convertView.findViewById(R.id.tv_person_name);
            TextView tvOrganizationName = convertView.findViewById(R.id.tv_organization_name);
            TextView tvServiceCity = convertView.findViewById(R.id.tv_service_city);
            TextView tvServiceArea = convertView.findViewById(R.id.tv_service_area);
            TextView tvPersonSpecialization = convertView.findViewById(R.id.tv_person_specialization);
            TextView tvPhoneNumber = convertView.findViewById(R.id.tv_phone_number);

            log(item);

// Set the data for each TextView
            tvPersonName.setText(item.get("person_name"));
            tvOrganizationName.setText(item.get("organization_name"));
            tvServiceCity.setText(item.get("service_city"));
            tvServiceArea.setText(item.get("service_area"));
            tvPersonSpecialization.setText(item.get("person_specialization"));
            tvPhoneNumber.setText(item.get("phone_number"));


            // Set button actions
            btnAccept.setOnClickListener(v -> updateStatus(item.get("id"), "approve"));
            btnReject.setOnClickListener(v -> updateStatus(item.get("id"), "reject"));

            return convertView;
        }

        private void updateStatus(String id, String action) {
            Map<String, String> params = new HashMap<>();
            params.put("update_status", action);
            params.put("id", id);

            // Network request to update the status
            new Internet3(activity, CustomTools.url("es.php"), params, (code, result) -> {
                if (code == 200) {
                    // Update the list by removing the item or refreshing the list
                    CustomTools.toast(activity, action.equals("approve") ? "Verified" : "Rejected");
                    fetchUnverifiedData(); // Refresh the data
                } else {
                    CustomTools.toast(activity, "Failed to update status.");
                }
            }).connect();
        }
    }




}
