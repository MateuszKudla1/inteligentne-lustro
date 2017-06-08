package com.example.mateusz.inteligentnelustro;



        import com.android.volley.Response;
        import com.android.volley.toolbox.StringRequest;

        import java.util.HashMap;
        import java.util.Map;

public class RegisterRequest extends StringRequest {
    private String zapas = "https://mateuszkudla15.000webhostapp.com/Register.php";
    private static final String REGISTER_REQUEST_URL = "http://smartmirror94.pe.hu/Reister.php";
    private Map<String, String> params;

    public RegisterRequest(String name, String email, String password, Response.Listener<String> listener) {
        super(Method.POST, REGISTER_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("name", name);

        params.put("email", email);
        params.put("password", password);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}