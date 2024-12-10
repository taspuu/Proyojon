package bd.com.taspi.apps;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class RequestForListing extends Activity {

    private TextView tvPersonSpecialization;
    private Spinner spinnerServiceType, spinnerDistName, spinnerPersonSpecialization;
    private EditText editTextOrganizationName, editTextPhoneNumber, editTextPersonName, editTextServiceArea;
    private Button buttonSubmit;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_for_listing);

        // Initialize UI components
        spinnerServiceType = findViewById(R.id.spinnerServiceType);
        spinnerDistName = findViewById(R.id.spinnerDistName);
        spinnerPersonSpecialization = findViewById(R.id.spinnerPersonSpecialization);
        tvPersonSpecialization = findViewById(R.id.tvPersonSpecialization);
        editTextOrganizationName = findViewById(R.id.editTextOrganizationName);
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        editTextPersonName = findViewById(R.id.editTextPersonName);
        editTextServiceArea = findViewById(R.id.editTextServiceArea);
        buttonSubmit = findViewById(R.id.buttonSubmit);
        progressBar = findViewById(R.id.progressBar);

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

        // Set the button click listener
        buttonSubmit.setOnClickListener(v -> submitData());
    }

    private void populateServiceTypeSpinner() {
        ArrayList<Map<String, String>> services = StaticData.getMyAppServices();
        ArrayList<String> serviceTitles = new ArrayList<>();

        for (Map<String, String> service : services) {
            if (!Objects.equals(service.get("id"), "0")) {
                serviceTitles.add(service.get("title"));
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, serviceTitles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerServiceType.setAdapter(adapter);
    }

    private void populateDistrictSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, StaticData.bangladeshDistricts);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDistName.setAdapter(adapter);
    }

    private void updateSpecializationSpinner() {
        String selectedService = spinnerServiceType.getSelectedItem().toString();
        String[] specializationOptions;

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
        progressBar.setVisibility(View.VISIBLE);

        ArrayList<Map<String, String>> services = StaticData.getMyAppServices();
        String serviceType = null;

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

        if (spinnerPersonSpecialization.getVisibility() == View.VISIBLE) {
            personSpecialization = spinnerPersonSpecialization.getSelectedItem().toString();
        }

        if (phoneNumber.isEmpty()) {
            CustomTools.toast(this, "Please fill in all required fields.");
            progressBar.setVisibility(View.GONE);
            return;
        }

        Map<String, String> formData = new HashMap<>();
        formData.put("service_type", serviceType);
        formData.put("service_city", districtName);
        formData.put("organization_name", organizationName);
        formData.put("phone_number", phoneNumber);
        formData.put("person_name", personName);
        formData.put("service_area", serviceArea);
        formData.put("person_specialization", personSpecialization);
        formData.put("add_es", "2");

        new Internet3(this, CustomTools.url("es.php"), formData, (code, result) -> {
            progressBar.setVisibility(View.GONE);
            if (code == 200) {
                CustomTools.toast(RequestForListing.this, "Data submitted successfully!");
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        finishAffinity();
                    }
                }, 1000);
            } else {
                CustomTools.toast(RequestForListing.this, "Failed to submit data.");
            }
        }).connect();
    }
}
