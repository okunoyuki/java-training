package clock;

import static clock.NodeTools.FONT_SMALL;
import static clock.NodeTools.FONT_TINY;
import static clock.NodeTools.hideNode;
import static clock.NodeTools.ensureVisibleInScrollPane;
import static clock.NodeTools.createHBox;
import static clock.NodeTools.createIconBtn;
import static clock.NodeTools.createVBox;
import static clock.NodeTools.wrapWithScrollPane;
import static clock.NodeTools.showAlertAndWait;

import java.awt.Toolkit;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import clock.CountdownTimer.TimerPurpose;
import clock.CountdownTimer.TimerType;
import clock.NodeTools.IconFont;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {

    private static final String BTN_TXT_START = "\uf04b";
    private static final String BTN_TXT_PAUSE = "\uf04c";
    private static final String BTN_TXT_SKIP = "\uf051";
    private static final String BTN_TXT_STOP = "\uf04d";
    private static final String BTN_TXT_ADD_TIMER = "\uf0fe";
    private static final String BTN_TXT_REPORT = "\uf15c";
    private static final String BTN_TXT_TRASH = "\uf1f8";
    private static final double WINDOW_PREF_WIDTH = 800;
    private static final double WINDOW_PREF_HEIGHT = 700;
    private static final double WINDOW_MIN_WIDTH = 340;
    private static final double WINDOW_MIN_HEIGHT = 535;
    private static final ColorSet BG_COLOR_SET = ColorSet.GRAY;
    private static final Color BG_COLOR = BG_COLOR_SET.lightColor();
    private static final String INFO_REPORT_TITLE = "Good job!";
    private static final String ALERT_SWITCH_TIMERS_TITLE = "Time's up!";
    private static final String ALERT_SWITCH_TIMERS_CONTENT = "Time to ";
    private static final String ALERT_MAX_MINUTE_INPUT_ERROR_TITLE = "You can't!";
    private static final String ALERT_MAX_MINUTE_INPUT_ERROR_CONTENT = "Set time between 1 and 999.";
    private static final String ALERT_TIMER_NAME_INPUT_ERROR_TITLE = "You can't!";
    private static final String ALERT_TIMER_NAME_INPUT_ERROR_CONTENT = "Too long!";
    private static final String ALERT_TOO_MANY_TIMERS_TITLE = "You can't!";
    private static final String ALERT_TOO_MANY_TIMERS_CONTENT = "You cannot have more than 10 timers.";
    private static final String ALERT_TIMER_DELETE_REJECT_TITLE = "You can't!";
    private static final String ALERT_TIMER_DELETE_REJECT_CONTENT = "You MUST have at least 1 work timer and 1 rest timer.";

    private Clock clock;
    private PomodoroController pomoCtrl;
    private Timer timer;
    private boolean initialized = false;
    private boolean isDeleting = false;
    private ColorSet currentColorSet;
    private HBox timerBox;
    private ScrollPane timerScrlPane;
    private BorderPane rootBox;
    private Label dateLabel;
    private Label timeLabel;
    private Label trashBtn;
    private Label reportBtn;
    private Label startOrPauseBtn;
    private Label skipBtn;
    private Label stopBtn;
    private Label addWorkTimerBtn;
    private Label addRestTimerBtn;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        if (initialized) {
            throw new IllegalStateException("Already initialized.");
        }
        pomoCtrl = createPomoCtrl();
        currentColorSet = pomoCtrl.currentTimer().getColorSet();
        clock = new Clock(FONT_TINY, FONT_SMALL,currentColorSet.darkColor());

        timerBox = createHBox(Pos.CENTER, pomoCtrl.getNodes());
        timerScrlPane = wrapWithScrollPane(timerBox);
        timerScrlPane.setStyle("-fx-background-color:transparent;");
        timerScrlPane.getStyleClass().add("scroll-pane");
        timeLabel = clock.getTimeNode();

        trashBtn = createTrashBtn();
        trashBtn.setPadding(new Insets(0, 15, 0, 0));
        reportBtn = createReportBtn();
        BorderPane topBox = new BorderPane();
        dateLabel = clock.getDateNode();
        topBox.setLeft(dateLabel);
        topBox.setRight(createHBox(Pos.TOP_RIGHT, trashBtn, reportBtn));

        startOrPauseBtn = createStartOrPauseBtn();
        skipBtn = createSkipBtn();
        stopBtn = createStopBtn();
        addWorkTimerBtn = createAddWorkTimerBtn();
        addRestTimerBtn = createAddRestTimerBtn();
        HBox ctrlBtns = createHBox(Pos.CENTER, stopBtn, startOrPauseBtn, skipBtn);
        ctrlBtns.setSpacing(30);

        HBox timerBtns = createHBox(Pos.CENTER_RIGHT, addWorkTimerBtn, addRestTimerBtn);
        VBox bottomBox = createVBox(null, timerBtns);

        rootBox = new BorderPane();
        rootBox.setPadding(new Insets(10, 20, 10, 10));
        rootBox.setTop(topBox);
        rootBox.setCenter(createVBox(Pos.CENTER, timeLabel, timerScrlPane, ctrlBtns));
        rootBox.setBottom(bottomBox);
        rootBox.widthProperty().addListener((observable, oldValue, newValue) -> {
            double btnWidth = addRestTimerBtn.getWidth() + skipBtn.getWidth() + stopBtn.getWidth() + (addWorkTimerBtn.getWidth() + addRestTimerBtn.getWidth()) * 2;
            if (btnWidth >= rootBox.getWidth()) {
                hideNode(timerBtns, false);
            } else {
                hideNode(timerBtns, true);
            }
        });
        rootBox.heightProperty().addListener((observable, oldValue, newValue) -> {
            if (rootBox.getHeight() <= WINDOW_MIN_HEIGHT - 10) {
                hideNode(topBox, false);
                hideNode(bottomBox, false);
            } else {
                hideNode(topBox, true);
                hideNode(bottomBox, true);
            }
        });

        Scene scene = new Scene(rootBox);
        scene.getStylesheets().add("clock/css/main.css");

        rootBox.setStyle("-fx-background-color: " + BG_COLOR_SET.toRGBTxt(BG_COLOR) + ";");

        timer = createAndSetupTimer();

        setUpStage(primaryStage, scene);
        primaryStage.show();
        initialized = true;
    }

    private PomodoroController createPomoCtrl() {
        if (initialized) {
            throw new IllegalStateException("Already initialized.");
        }

        PomodoroController pomoCtrl = new PomodoroController(BG_COLOR);
        pomoCtrl.onTimerFinished((oldTimer, newTimer) -> {
            Toolkit.getDefaultToolkit().beep();
            showSwitchTimerAlert(newTimer.getTimerPurpose());
            pomoCtrl.deselectActiveTimer();
            selectNextTimer();
            pomoCtrl.start();
            ensureVisibleInScrollPane(timerScrlPane, newTimer.getNode());
        });
        pomoCtrl.onInvalidInputForMaxMinute(t -> showMaxMinuteInputErrorAlert());
        pomoCtrl.onInvalidInputForTimerName(t -> showTimerNameInputErrorAlert());
        pomoCtrl.onTimerDeleteBtnSelected(timer -> {
            if (!pomoCtrl.canDeleteTimer(timer)) {
                showTimerDeleteRejectionAlert();
                return;
            }
            pomoCtrl.deleteTimer(timer);
        });
        pomoCtrl.onTimerDeleted(timer -> {
            timerBox.getChildren().clear();
            timerBox.getChildren().addAll(pomoCtrl.getNodes());
        });
        return pomoCtrl;
    }

    private Label createReportBtn() {
        if (initialized) {
            throw new IllegalStateException("Already initialized.");
        }

        Label btn = createIconBtn(BTN_TXT_REPORT, IconFont.MEDIUM);
        btn.setOnMouseClicked(event -> onClickReportBtn());
        setColorOnLabel(btn, ColorSet.GRAY);
        return btn;
    }

    private Label createTrashBtn() {
        if (initialized) {
            throw new IllegalStateException("Already initialized.");
        }

        Label btn = createIconBtn(BTN_TXT_TRASH, IconFont.MEDIUM);
        btn.setOnMouseClicked(event -> onClickTrashBtn());
        setColorOnLabel(btn, ColorSet.GRAY);
        return btn;
    }

    private Label createStartOrPauseBtn() {
        if (initialized) {
            throw new IllegalStateException("Already initialized.");
        }

        Label btn = createIconBtn(BTN_TXT_START, IconFont.LARGE);
        btn.setOnMouseClicked(event -> onClickPomoCtrlBtn());
        setColorOnLabel(btn);
        return btn;
    }

    private Label createSkipBtn() {
        if (initialized) {
            throw new IllegalStateException("Already initialized.");
        }

        Label btn = createIconBtn(BTN_TXT_SKIP, IconFont.LARGE);
        btn.setOnMouseClicked(event -> onClickSkipBtn());
        setColorOnLabel(btn);
        return btn;
    }

    private Label createStopBtn() {
        if (initialized) {
            throw new IllegalStateException("Already initialized.");
        }

        Label btn = createIconBtn(BTN_TXT_STOP, IconFont.LARGE);
        btn.setOnMouseClicked(event -> onClickPomoResetBtn());
        setColorOnLabel(btn);
        return btn;
    }

    private Label createAddWorkTimerBtn() {
        if (initialized) {
            throw new IllegalStateException("Already initialized.");
        }

        Label btn = createIconBtn(BTN_TXT_ADD_TIMER, IconFont.LARGE);
        btn.setOnMouseClicked(event -> onClickPomoAddTimerBtn());
        setColorOnLabel(btn, ColorSet.BLUE);
        return btn;
    }

    private Label createAddRestTimerBtn() {
        if (initialized) {
            throw new IllegalStateException("Already initialized.");
        }

        Label btn = createIconBtn(BTN_TXT_ADD_TIMER, IconFont.LARGE);
        btn.setOnMouseClicked(event -> onClickPomoAddRestBtn());
        setColorOnLabel(btn, ColorSet.YELLOW);
        return btn;
    }

    private void setColorOnLabel(Label label) {
        if (currentColorSet == null) {
            throw new IllegalStateException("currentColorSet is null.");
        }
        label.setTextFill(currentColorSet.lightColor());
        label.setOnMouseEntered(event -> label.setTextFill(currentColorSet.darkColor()));
        label.setOnMouseExited(event -> label.setTextFill(currentColorSet.lightColor()));
    }

    private void setColorOnLabel(Label label, ColorSet colorSet) {
        if (colorSet == ColorSet.GRAY) {
            label.setTextFill(colorSet.saturatedDarkColor());
            label.setOnMouseEntered(event -> label.setTextFill(colorSet.darkColor()));
            label.setOnMouseExited(event -> label.setTextFill(colorSet.saturatedDarkColor()));
        } else {
            label.setTextFill(colorSet.lightColor());
            label.setOnMouseEntered(event -> label.setTextFill(colorSet.darkColor()));
            label.setOnMouseExited(event -> label.setTextFill(colorSet.lightColor()));
        }
    }

    private Timer createAndSetupTimer() {
        if (initialized) {
            throw new IllegalStateException("Already initialized.");
        }

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if (clock == null || pomoCtrl == null) {
                        throw new IllegalStateException("Not ready yet.");
                    }
                    clock.update();
                    if (pomoCtrl.isActive()) {
                        pomoCtrl.update();
                    }
                });
            }
        }, 0, 500);
        return timer;
    }

    private void setUpStage(Stage stage, Scene scene) {
        if (initialized) {
            throw new IllegalStateException("Already initialized.");
        }

        stage.setScene(scene);
        stage.setTitle("Pomopomo Timer");
        stage.setOnCloseRequest(evet -> {
            timer.cancel();
        });
        stage.setHeight(WINDOW_PREF_HEIGHT);
        stage.setWidth(WINDOW_PREF_WIDTH);
        stage.setMinHeight(WINDOW_MIN_HEIGHT);
        stage.setMinWidth(WINDOW_MIN_WIDTH);
    }

    private void onClickTrashBtn() {
        if (isDeleting) {
            hideOrShowOnDeletingMode(false);
            isDeleting = false;
        } else {
            hideOrShowOnDeletingMode(true);
            isDeleting = true;
        }
    }

    private void hideOrShowOnDeletingMode(boolean hide) {
        reportBtn.setVisible(!hide);
        pomoCtrl.setVisibleOnDeleteBtn(hide);
        dateLabel.setVisible(!hide);
        timeLabel.setVisible(!hide);
        stopBtn.setVisible(!hide);
        startOrPauseBtn.setVisible(!hide);
        skipBtn.setVisible(!hide);
        addWorkTimerBtn.setVisible(!hide);
        addRestTimerBtn.setVisible(!hide);
    }

    private void onClickReportBtn() {
        showTimerReport(pomoCtrl.getReports());
    }

    private void onClickPomoCtrlBtn() {
        if (pomoCtrl.isActive()) {
            startOrPauseBtn.setText(BTN_TXT_START);
            stopBtn.setVisible(true);
            pomoCtrl.pause();
        } else {
            startOrPauseBtn.setText(BTN_TXT_PAUSE);
            stopBtn.setVisible(false);
            pomoCtrl.start();
        }
    }

    private void onClickSkipBtn() {
        if (pomoCtrl.isActive()) {
            pomoCtrl.pause();
            resetAndDeselectCurrentTimer();
            selectNextTimer();
            pomoCtrl.start();
        } else {
            resetAndDeselectCurrentTimer();
            selectNextTimer();
        }
    }

    private void resetAndDeselectCurrentTimer() {
        pomoCtrl.reset();
        pomoCtrl.deselectCurrent();
    }

    private void selectNextTimer() {
        pomoCtrl.selectNext();
        changeColors(pomoCtrl.currentTimer().getColorSet());
    }

    private void selectTimer(int at) {
        pomoCtrl.select(at);
        changeColors(pomoCtrl.currentTimer().getColorSet());
    }

    private void changeColors(ColorSet colorSet) {
        currentColorSet = colorSet;
        startOrPauseBtn.setTextFill(currentColorSet.lightColor());
        stopBtn.setTextFill(currentColorSet.lightColor());
        skipBtn.setTextFill(currentColorSet.lightColor());
        clock.changeTextColor(currentColorSet.darkColor());
    }

    private void onClickPomoResetBtn() {
        resetAndDeselectCurrentTimer();
        showTimerReport(pomoCtrl.getReports());
        pomoCtrl.clearAllHistory();
        selectTimer(0);
    }

    private void onClickPomoAddTimerBtn() {
        if (!pomoCtrl.canAddMoreTimer()) {
            showTooManyTimerAlert();
            return;
        }
        Platform.runLater(() -> {
            Node newTimer = pomoCtrl.createNewTimer(TimerType.WORK_BLUE, BG_COLOR);
            timerBox.getChildren().add(newTimer);
            ensureVisibleInScrollPane(timerScrlPane, newTimer);
        });
    }

    private void onClickPomoAddRestBtn() {
        if (!pomoCtrl.canAddMoreTimer()) {
            showTooManyTimerAlert();
            return;
        }
        Platform.runLater(() -> {
            Node newTimer = pomoCtrl.createNewTimer(TimerType.REST_YELLOW, BG_COLOR);
            timerBox.getChildren().add(newTimer);
            ensureVisibleInScrollPane(timerScrlPane, newTimer);
        });
    }

    private void showTimerReport(List<TimerReport> reports) {
        String reportStr = "";
        for (int i = 0; i < reports.size(); i++) {
            reportStr += reports.get(i).toString() + "\n";
        }
        showAlertAndWait(INFO_REPORT_TITLE, reportStr, AlertType.INFORMATION, true);
    }

    private void showSwitchTimerAlert(TimerPurpose purpose) {
        showAlertAndWait(ALERT_SWITCH_TIMERS_TITLE, ALERT_SWITCH_TIMERS_CONTENT + purpose.verb(), AlertType.INFORMATION, true);
    }

    private void showMaxMinuteInputErrorAlert() {
        showAlertAndWait(ALERT_MAX_MINUTE_INPUT_ERROR_TITLE, ALERT_MAX_MINUTE_INPUT_ERROR_CONTENT, AlertType.INFORMATION, false);
    }

    private void showTimerNameInputErrorAlert() {
        showAlertAndWait(ALERT_TIMER_NAME_INPUT_ERROR_TITLE, ALERT_TIMER_NAME_INPUT_ERROR_CONTENT, AlertType.INFORMATION, false);
    }

    private void showTooManyTimerAlert() {
        showAlertAndWait(ALERT_TOO_MANY_TIMERS_TITLE, ALERT_TOO_MANY_TIMERS_CONTENT, AlertType.INFORMATION, false);
    }

    private void showTimerDeleteRejectionAlert() {
        showAlertAndWait(ALERT_TIMER_DELETE_REJECT_TITLE, ALERT_TIMER_DELETE_REJECT_CONTENT, AlertType.INFORMATION, false);
    }
}
