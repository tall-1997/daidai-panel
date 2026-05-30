package com.daidai.app.ui.screen.home;

import androidx.lifecycle.ViewModel;
import com.daidai.app.data.remote.model.*;
import com.daidai.app.data.repository.TaskRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.flow.StateFlow;
import javax.inject.Inject;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010$\n\u0002\b\u001f\b\u0086\b\u0018\u00002\u00020\u0001B\u0087\u0001\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\u000e\b\u0002\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006\u0012\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\t\u0012\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\t\u0012\b\b\u0002\u0010\u000b\u001a\u00020\f\u0012\b\b\u0002\u0010\r\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u000e\u001a\u00020\t\u0012\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u0007\u0012\u001a\b\u0002\u0010\u0010\u001a\u0014\u0012\u0004\u0012\u00020\f\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\u00060\u0011\u00a2\u0006\u0002\u0010\u0012J\t\u0010!\u001a\u00020\u0003H\u00c6\u0003J\u001b\u0010\"\u001a\u0014\u0012\u0004\u0012\u00020\f\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\u00060\u0011H\u00c6\u0003J\t\u0010#\u001a\u00020\u0003H\u00c6\u0003J\u000f\u0010$\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006H\u00c6\u0003J\u000b\u0010%\u001a\u0004\u0018\u00010\tH\u00c6\u0003J\u000b\u0010&\u001a\u0004\u0018\u00010\tH\u00c6\u0003J\t\u0010\'\u001a\u00020\fH\u00c6\u0003J\t\u0010(\u001a\u00020\u0003H\u00c6\u0003J\t\u0010)\u001a\u00020\tH\u00c6\u0003J\u000b\u0010*\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\u008b\u0001\u0010+\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\u000e\b\u0002\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\t2\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\t2\b\b\u0002\u0010\u000b\u001a\u00020\f2\b\b\u0002\u0010\r\u001a\u00020\u00032\b\b\u0002\u0010\u000e\u001a\u00020\t2\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u00072\u001a\b\u0002\u0010\u0010\u001a\u0014\u0012\u0004\u0012\u00020\f\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\u00060\u0011H\u00c6\u0001J\u0013\u0010,\u001a\u00020\u00032\b\u0010-\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010.\u001a\u00020\fH\u00d6\u0001J\t\u0010/\u001a\u00020\tH\u00d6\u0001R\u0011\u0010\u000b\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0013\u0010\b\u001a\u0004\u0018\u00010\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0011\u0010\r\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\u0018R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0004\u0010\u0018R\u0011\u0010\u000e\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0016R\u0013\u0010\u000f\u001a\u0004\u0018\u00010\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u001bR\u0013\u0010\n\u001a\u0004\u0018\u00010\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u0016R#\u0010\u0010\u001a\u0014\u0012\u0004\u0012\u00020\f\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\u00060\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001eR\u0017\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010 \u00a8\u00060"}, d2 = {"Lcom/daidai/app/ui/screen/home/TaskListUiState;", "", "isLoading", "", "isRefreshing", "tasks", "", "Lcom/daidai/app/data/remote/model/Task;", "error", "", "successMessage", "currentPage", "", "hasMore", "searchQuery", "selectedTask", "taskLogs", "", "(ZZLjava/util/List;Ljava/lang/String;Ljava/lang/String;IZLjava/lang/String;Lcom/daidai/app/data/remote/model/Task;Ljava/util/Map;)V", "getCurrentPage", "()I", "getError", "()Ljava/lang/String;", "getHasMore", "()Z", "getSearchQuery", "getSelectedTask", "()Lcom/daidai/app/data/remote/model/Task;", "getSuccessMessage", "getTaskLogs", "()Ljava/util/Map;", "getTasks", "()Ljava/util/List;", "component1", "component10", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", "other", "hashCode", "toString", "app_debug"})
public final class TaskListUiState {
    private final boolean isLoading = false;
    private final boolean isRefreshing = false;
    @org.jetbrains.annotations.NotNull
    private final java.util.List<com.daidai.app.data.remote.model.Task> tasks = null;
    @org.jetbrains.annotations.Nullable
    private final java.lang.String error = null;
    @org.jetbrains.annotations.Nullable
    private final java.lang.String successMessage = null;
    private final int currentPage = 0;
    private final boolean hasMore = false;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String searchQuery = null;
    @org.jetbrains.annotations.Nullable
    private final com.daidai.app.data.remote.model.Task selectedTask = null;
    @org.jetbrains.annotations.NotNull
    private final java.util.Map<java.lang.Integer, java.util.List<java.lang.String>> taskLogs = null;
    
    public TaskListUiState(boolean isLoading, boolean isRefreshing, @org.jetbrains.annotations.NotNull
    java.util.List<com.daidai.app.data.remote.model.Task> tasks, @org.jetbrains.annotations.Nullable
    java.lang.String error, @org.jetbrains.annotations.Nullable
    java.lang.String successMessage, int currentPage, boolean hasMore, @org.jetbrains.annotations.NotNull
    java.lang.String searchQuery, @org.jetbrains.annotations.Nullable
    com.daidai.app.data.remote.model.Task selectedTask, @org.jetbrains.annotations.NotNull
    java.util.Map<java.lang.Integer, ? extends java.util.List<java.lang.String>> taskLogs) {
        super();
    }
    
    public final boolean isLoading() {
        return false;
    }
    
    public final boolean isRefreshing() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.List<com.daidai.app.data.remote.model.Task> getTasks() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String getError() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String getSuccessMessage() {
        return null;
    }
    
    public final int getCurrentPage() {
        return 0;
    }
    
    public final boolean getHasMore() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getSearchQuery() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final com.daidai.app.data.remote.model.Task getSelectedTask() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.Map<java.lang.Integer, java.util.List<java.lang.String>> getTaskLogs() {
        return null;
    }
    
    public TaskListUiState() {
        super();
    }
    
    public final boolean component1() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.Map<java.lang.Integer, java.util.List<java.lang.String>> component10() {
        return null;
    }
    
    public final boolean component2() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.List<com.daidai.app.data.remote.model.Task> component3() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String component4() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String component5() {
        return null;
    }
    
    public final int component6() {
        return 0;
    }
    
    public final boolean component7() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component8() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final com.daidai.app.data.remote.model.Task component9() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final com.daidai.app.ui.screen.home.TaskListUiState copy(boolean isLoading, boolean isRefreshing, @org.jetbrains.annotations.NotNull
    java.util.List<com.daidai.app.data.remote.model.Task> tasks, @org.jetbrains.annotations.Nullable
    java.lang.String error, @org.jetbrains.annotations.Nullable
    java.lang.String successMessage, int currentPage, boolean hasMore, @org.jetbrains.annotations.NotNull
    java.lang.String searchQuery, @org.jetbrains.annotations.Nullable
    com.daidai.app.data.remote.model.Task selectedTask, @org.jetbrains.annotations.NotNull
    java.util.Map<java.lang.Integer, ? extends java.util.List<java.lang.String>> taskLogs) {
        return null;
    }
    
    @java.lang.Override
    public boolean equals(@org.jetbrains.annotations.Nullable
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override
    public int hashCode() {
        return 0;
    }
    
    @java.lang.Override
    @org.jetbrains.annotations.NotNull
    public java.lang.String toString() {
        return null;
    }
}