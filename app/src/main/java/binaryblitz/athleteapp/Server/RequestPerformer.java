package binaryblitz.athleteapp.Server;

import org.json.JSONObject;

import binaryblitz.athleteapp.Data.ProfessionalType;

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

    public RequestExecutor like(String id) {
        return new RequestExecutor(new LikeRequest(id), listener, objects);
    }

    public RequestExecutor deleteLike(String id) {
        return new RequestExecutor(new DeleteLikeRequest(id), listener, objects);
    }

    public RequestExecutor follow(String id) {
        return new RequestExecutor(new FollowRequest(id), listener, objects);
    }

    public RequestExecutor unfollow(String id) {
        return new RequestExecutor(new UnfollowRequest(id), listener, objects);
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

    public RequestExecutor trainer(String id) {
        return new RequestExecutor(new GetTrainerRequest(id), listener, objects);
    }

    public RequestExecutor trainerPosts(String id) {
        return new RequestExecutor(new GetTrainerPostsRequest(id), listener, objects);
    }

    public RequestExecutor trainerPrograms(String id) {
        return new RequestExecutor(new GetTrainerProgramsRequest(id), listener, objects);
    }

    public RequestExecutor programs() {
        return new RequestExecutor(new GetProgramsRequest(), listener, objects);
    }

    public RequestExecutor post(String id) {
        return new RequestExecutor(new GetPostRequest(id), listener, objects);
    }

    public RequestExecutor program(String id) {
        return new RequestExecutor(new GetProgramRequest(id), listener, objects);
    }

    public RequestExecutor workoutSessions() {
        return new RequestExecutor(new GetWorkoutSessions(), listener, objects);
    }

    public RequestExecutor workouts() {
        return new RequestExecutor(new GetWorkoutsRequest(), listener, objects);
    }

    public RequestExecutor filterTrainers(ProfessionalType type, String spec, String order) {
        return new RequestExecutor(new FilterTrainersRequest(type, order, spec), listener, objects);
    }

    public RequestExecutor subscriptions() {
        return new RequestExecutor(new GetSubscriptionsRequest(), listener, objects);
    }

    public RequestExecutor sendMessage(String id, String photo, String content) {
        return new RequestExecutor(new SendMessageRequest(id, content, photo), listener, objects);
    }

    public RequestExecutor updateUser() {
        return new RequestExecutor(new UpdateUserRequest(), listener, objects);
    }

    public RequestExecutor specializations() {
        return new RequestExecutor(new GetCategoriesRequest(), listener, objects);
    }

    public RequestExecutor getUser() {
        return new RequestExecutor(new GetUserRequest(), listener, objects);
    }

    public RequestExecutor trainers(ProfessionalType type) {
        return new RequestExecutor(new GetProfsRequest(type), listener, objects);
    }

    public RequestExecutor statistics(String id) {
        return new RequestExecutor(new GetUserStatsRequest(id), listener, objects);
    }

    public RequestExecutor messages(String id) {
        return new RequestExecutor(new GetMessagesRequest(id), listener, objects);
    }

    public RequestExecutor rateTrainer(String id) {
        return new RequestExecutor(new RateTrainerRequest(id), listener, objects);
    }

    public RequestExecutor subscript(String id) {
        return new RequestExecutor(new GetSubscriptionRequest(id), listener, objects);
    }

    public RequestExecutor rateProgram(String id) {
        return new RequestExecutor(new RateProgramRequest(id), listener, objects);
    }

    public RequestExecutor getComments(String id) {
        return new RequestExecutor(new GetCommentsRequest(id), listener, objects);
    }

    public RequestExecutor createComment(String id) {
        return new RequestExecutor(new CreateCommentRequest(id), listener, objects);
    }

    public RequestExecutor deleteComment(String id) {
        return new RequestExecutor(new DeleteCommentRequest(id), listener, objects);
    }
}
