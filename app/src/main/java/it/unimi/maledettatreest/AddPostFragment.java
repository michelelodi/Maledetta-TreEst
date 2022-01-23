package it.unimi.maledettatreest;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import org.json.JSONObject;
import it.unimi.maledettatreest.controller.CommunicationController;
import it.unimi.maledettatreest.model.LinesModel;
import it.unimi.maledettatreest.services.KeyboardManager;

public class AddPostFragment extends Fragment {

    private final static String TAG = MainActivity.TAG_BASE + "AddPostFragment";
    public final static String NO_DELAY = "on time";
    public final static String SHORT_DELAY = "<15 minutes";
    public final static String LONG_DELAY = "15+ minutes";
    public final static String CANCELLED = "cancelled";
    public final static String IDEAL = "Ideal";
    public final static String ACCEPTABLE = "Acceptable";
    public final static String UNACCEPTABLE = "Serious problems";

    private CommunicationController cc;
    private LinesModel lm;
    private KeyboardManager km;
    private RadioGroup radioGroupDelay;
    private RadioGroup radioGroupStatus;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        cc = CommunicationController.getInstance(requireContext());
        lm = LinesModel.getInstance(requireContext());
        km = new KeyboardManager(requireActivity());
        return inflater.inflate(R.layout.fragment_add_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        radioGroupDelay = view.findViewById(R.id.radioGroupDelay);
        radioGroupStatus = view.findViewById(R.id.radioGroupStatus);

        view.findViewById(R.id.postB).setOnClickListener(v-> {
            km.hideKeyboard();

            Integer idxDelay = radioGroupDelay.indexOfChild(radioGroupDelay.
                                            findViewById(radioGroupDelay.getCheckedRadioButtonId()));

            Integer idxStatus = radioGroupStatus.indexOfChild(radioGroupStatus.
                    findViewById(radioGroupStatus.getCheckedRadioButtonId()));

            String comment = ((EditText)view.findViewById(R.id.commentET)).getText().toString();
            String message = "";

            if(comment.isEmpty())
                comment = null;
            else if(comment.length() > 100)
                message = "Comment must be shorter than 100 characters";

            if(idxDelay == -1 && idxStatus == -1 && comment == null)
                message = "You must compile at least one field";

            if(message.isEmpty()) {
                if(idxDelay == -1) idxDelay = null;
                if(idxStatus == -1) idxStatus = null;
                cc.addPost(lm.getSelectedDir().getDid(),
                        String.valueOf(idxDelay),
                        String.valueOf(idxStatus),
                        comment,
                        this::handleAddPostResponse,
                        error -> cc.handleVolleyError(error, requireContext(), TAG));
            }
            else
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
        });
    }

    private void handleAddPostResponse(JSONObject response) {
        Toast.makeText(requireContext(), "Post successfully created", Toast.LENGTH_LONG).show();

        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                .navigate(AddPostFragmentDirections.actionAddPostFragmentToBoardFragment());
    }
}