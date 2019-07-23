package io.smallrye.reactive.operators;

import io.smallrye.reactive.Uni;
import io.smallrye.reactive.groups.*;
import io.smallrye.reactive.subscription.UniSubscription;
import io.smallrye.reactive.tuples.Pair;
import org.reactivestreams.Publisher;

import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static io.smallrye.reactive.helpers.ParameterValidation.nonNull;

public abstract class AbstractUni<T> implements Uni<T> {


    abstract void subscribing(UniSerializedSubscriber<? super T> subscriber);

    @Override
    public UniSubscribe<T> subscribe() {
        return new UniSubscribe<>(this);
    }

    @Override
    public UniOnResult<T> onResult() {
        return new UniOnResult<>(this);
    }

    @Override
    public UniOnTimeout<T> onNoResult() {
        return new UniOnTimeout<>(this, null, null);
    }

    @Override
    public UniOnNullResult<T> onNullResult() {
        return new UniOnNullResult<>(this);
    }

    @Override
    public UniOnFailure<T> onFailure() {
        return new UniOnFailure<>(this, null);
    }

    @Override
    public UniOnFailure<T> onFailure(Predicate<? super Throwable> predicate) {
        return new UniOnFailure<>(this, predicate);
    }

    @Override
    public UniOnFailure<T> onFailure(Class<? extends Throwable> typeOfFailure ) {
        return new UniOnFailure<>(this, typeOfFailure::isInstance);
    }

    @Override
    public UniAndGroup<T> and() {
        return new UniAndGroup<>(this);
    }

    @Override
    public <T2> Uni<Pair<T, T2>> and(Uni<T2> other) {
        return new UniAndGroup<>(this).uni(nonNull(other, "other")).asPair();
    }

    @Override
    public UniOr or() {
        return new UniOr<>(this);
    }

    @Override
    public UniAwait<T> await() {
        return new UniAwait<>(this);
    }

    //TODO Should handleResultOn and handleFailureOn be part of the onFailure and onResult groups

    @Override
    public Uni<T> handleResultOn(Executor executor) {
        return new UniHandleResultOn<>(this, nonNull(executor, "executor"));
    }

    @Override
    public Uni<T> handleFailureOn(Executor executor) {
        return new UniHandleFailureOn<>(this, nonNull(executor, "executor"));
    }

    @Override
    public Uni<T> callSubscribeOn(Executor executor) {
        return new UniCallSubscribeOn<>(this, nonNull(executor, "executor"));
    }

    @Override
    public Uni<T> cache() {
        return new UniCache<>(this);
    }

    @Override
    public Uni<T> onTerminate(BiConsumer<? super T, Throwable> callback) {
        return new UniPeekOnEvent<>(this, null, null, null, null, nonNull(callback, "callback"));
    }

    @Override
    public Uni<T> onCancellation(Runnable callback) {
        return new UniPeekOnEvent<>(this, null, null, null, nonNull(callback, "callback"), null);
    }

    @Override
    public Uni<T> onSubscription(Consumer<? super UniSubscription> callback) {
        return new UniPeekOnEvent<>(this, nonNull(callback, "callback"), null, null, null, null);
    }

    @Override
    public UniAdapt<T> adapt() {
        return new UniAdapt<>(this);
    }
}
