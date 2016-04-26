package binaryblitz.athleteapp.Server;

import org.json.JSONObject;

public class RequestPerformer {

    private OnRequestPerformedListener listener;
    private JSONObject[] objects;

    public RequestPerformer listener(OnRequestPerformedListener listener) {
        this.listener = listener;
        return this;
    }

    public RequestPerformer objects(JSONObject... objects) {
        this.objects = objects;
        return this;
    }

    public RequestExecutor verify(String phone, String code, String token) {
        return new RequestExecutor(new VerifyRequest(token, code, phone), listener, objects);
    }

    public RequestExecutor auth(String phone) {
        return new RequestExecutor(new AuthRequest(phone), listener, objects);
    }

    public RequestExecutor createUser() {
        return new RequestExecutor(new CreateUserRequest(), listener, objects);
    }

    public RequestExecutor post(String id) {
        return new RequestExecutor(new GetNewsItemRequest(id), listener, objects);
    }

    public RequestExecutor like(String id) {
        return new RequestExecutor(new LikeRequest(id), listener, objects);
    }

    public RequestExecutor deleteLike(String id) {
        return new RequestExecutor(new DeleteLikeRequest(id), listener, objects);
    }

    public RequestExecutor vkAuth(String  token) {
        return new RequestExecutor(new VKAuthRequest(token), listener, objects);
    }

    public RequestExecutor fbAuth(String  token) {
        return new RequestExecutor(new FacebookAuthRequest(token), listener, objects);
    }

    public RequestExecutor posts() {
        return new RequestExecutor(new GetNewsRequest(), listener, objects);
    }

    public RequestExecutor programs() {
        return new RequestExecutor(new GetProgramsRequest(), listener, objects);
    }

    public RequestExecutor updateUser() {
        return new RequestExecutor(new UpdateUserRequest(), listener, objects);
    }

}
