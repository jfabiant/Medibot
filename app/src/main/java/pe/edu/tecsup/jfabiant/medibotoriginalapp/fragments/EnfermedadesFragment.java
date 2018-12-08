package pe.edu.tecsup.jfabiant.medibotoriginalapp.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import pe.edu.tecsup.jfabiant.medibotoriginalapp.R;
import pe.edu.tecsup.jfabiant.medibotoriginalapp.adapters.EnfermedadAdapter;
import pe.edu.tecsup.jfabiant.medibotoriginalapp.models.Enfermedad;
import pe.edu.tecsup.jfabiant.medibotoriginalapp.services.ApiService;
import pe.edu.tecsup.jfabiant.medibotoriginalapp.services.ApiServiceGeneratorUser;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EnfermedadesFragment extends Fragment {


    private final static String TAG = EnfermedadesFragment.class.getSimpleName();
    public RecyclerView listEnfermedades;

    public EnfermedadesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_enfermedades, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).setTitle("Enfermedades estudiadas");

        listEnfermedades = getView().findViewById(R.id.list_enfermedades);
        listEnfermedades.setLayoutManager(new LinearLayoutManager(getContext()));
        listEnfermedades.setAdapter(new EnfermedadAdapter());

        //Loading ...

        final ProgressDialog progressDialog = new ProgressDialog(getContext(),
                R.style.AppTheme_Dark_Dialog);

        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Cargando ...");
        progressDialog.show();

        // TODO: Implement your own authentication logic here.
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        initialize();
                        progressDialog.dismiss();
                    }
                }, 1000);

        //End loading ...

    }

    private void initialize () {

        ApiService service = ApiServiceGeneratorUser.createService(getContext(), ApiService.class);

        Call<List<Enfermedad>> call = service.getEnfermedades();

        call.enqueue(new Callback<List<Enfermedad>>() {
            @Override
            public void onResponse(Call<List<Enfermedad>> call, Response<List<Enfermedad>> response) {
                try {

                    int statusCode = response.code();
                    Log.d(TAG, "HTTP status code: " + statusCode);

                    if (response.isSuccessful()) {

                        List<Enfermedad> enfermedades = response.body();
                        Log.d(TAG, "Enfermedades: " + enfermedades);

                        EnfermedadAdapter adapter = (EnfermedadAdapter) listEnfermedades.getAdapter();
                        adapter.setEnfermedades(getContext(), enfermedades);
                        adapter.notifyDataSetChanged();

                    } else {
                        Log.e(TAG, "onError: " + response.errorBody().string());
                        throw new Exception("Error en el servicio");
                    }

                } catch (Throwable t) {
                    try {
                        Log.e(TAG, "onThrowable: " + t.toString(), t);
                        Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                    }catch (Throwable x){}
                }
            }

            @Override
            public void onFailure(Call<List<Enfermedad>> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }
}
