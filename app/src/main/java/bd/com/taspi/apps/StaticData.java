package bd.com.taspi.apps;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StaticData {

    public static String TAG = "errnos";
    public static String[] bangladeshDistricts = {
            "All", "Barguna", "Barishal", "Bhola", "Jhalokathi", "Patuakhali", "Pirojpur", "Mehindiganj",
            "Bandarban", "Brahmanbaria", "Chandpur", "Chattogram", "Cumilla", "Cox's Bazar",
            "Feni", "Khagrachari", "Lakshmipur", "Noakhali", "Rangamati", "Chittagong",
            "Dhaka", "Faridpur", "Gazipur", "Gopalganj", "Kishoreganj", "Madaripur",
            "Manikganj", "Munshiganj", "Narayanganj", "Narsingdi", "Rajbari", "Shariatpur",
            "Tangail", "Bagerhat", "Chuadanga", "Jashore", "Jhenaidah", "Khulna", "Kushtia",
            "Magura", "Meherpur", "Narail", "Satkhira", "Jamalpur", "Mymensingh", "Netrokona",
            "Sherpur", "Bogura", "Joypurhat", "Naogaon", "Natore", "Chapainawabganj", "Pabna",
            "Rajshahi", "Sirajganj", "Dinajpur", "Gaibandha", "Kurigram", "Lalmonirhat",
            "Nilphamari", "Panchagarh", "Rangpur", "Thakurgaon", "Habiganj", "Moulvibazar",
            "Sunamganj", "Sylhet"
    };

    public static String[] doctorsSpecialist = {
            "All",
            "Cardiologist",
            "Neurologist",
            "Orthopedic Surgeon",
            "Pediatrician",
            "General Surgeon",
            "Dermatologist",
            "ENT Specialist",
            "Gynecologist",
            "Urologist",
            "Psychiatrist",
            "Ophthalmologist",
            "Oncologist",
            "Nephrologist",
            "Endocrinologist",
            "Gastroenterologist",
            "Pulmonologist",
            "Hematologist",
            "Plastic Surgeon",
            "Radiologist",
            "Anesthesiologist",
            "Allergist",
            "Medicine specialist",
            "Pathologist"
    };

    public static String[] policeDesignations = {
            "All",
            "police commissioner",
            "Inspector General of Police (IGP)",
            "Additional Inspector General of Police",
            "Deputy Inspector General of Police (DIG)",
            "Assistant Inspector General of Police",
            "Superintendent of Police (SP)",
            "Additional Superintendent of Police",
            "Assistant Superintendent of Police (ASP)",
            "Inspector",
            "Sub-Inspector (SI)",
            "Assistant Sub-Inspector (ASI)",
            "Constable"
    };

    public static String[] rabDesignations = {
            "All",
            "Director General",
            "Additional Director General",
            "Director",
            "Deputy Director",
            "Assistant Director",
            "Senior Assistant Director",
            "Superintendent of Police (RAB)",
            "Sub-Inspector (RAB)",
            "Assistant Sub-Inspector (RAB)",
            "Constable (RAB)"
    };

    public static String[] fireServiceDesignations = {
            "All",
            "Director General",
            "Deputy Director",
            "Assistant Director",
            "Station Officer",
            "Senior Station Officer",
            "Firefighter",
            "Leading Firefighter",
            "Watchroom Operator",
            "Driver"
    };



    public static ArrayList<Map<String, String>> getMyAppServices() {
        ArrayList<Map<String, String>> serviceList = new ArrayList<>();
        // Add icons and titles
        serviceList.add(addService("POLICE","icon_police", "Police"));
        serviceList.add(addService("AMBULANCE", "icon_ambulance", "Ambulance"));
        serviceList.add(addService("DOCTORS", "icon_doctor", "Doctor"));
        serviceList.add(addService("HOSPITAL", "icon_hospital", "Hospital"));
        serviceList.add(addService("FIRE_SERVICE", "icon_fire", "Fire Service"));
        serviceList.add(addService("RAB", "icon_rab", "RAB"));
        serviceList.add(addService("2", "icon_add_1", "Request Listing"));
        serviceList.add(addService("0", "icon_national_emergency", "Call 999"));
        return serviceList;
    }

    // Method to add service data to the list
    private static Map<String, String> addService(@NonNull String idX, String iconName, String title) {
        Map<String, String> service = new HashMap<>();
        service.put("id", String.valueOf(idX));
        service.put("icon", iconName);
        service.put("title", title);
        return service;
    }
}
