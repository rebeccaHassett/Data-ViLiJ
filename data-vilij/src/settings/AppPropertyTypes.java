package settings;

/**
 * This enumerable type lists the various application-specific property types listed in the initial set of properties to
 * be loaded from the workspace properties <code>xml</code> file specified by the initialization parameters.
 *
 * @author Ritwik Banerjee
 * @see vilij.settings.InitializationParams
 */
public enum AppPropertyTypes {

    /* resource files and folders */
    DATA_RESOURCE_PATH,
    CSS_PATH,

    /* user interface icon file names */
    SCREENSHOT_ICON,
    RUN_ICON,
    CONFIG_ICON,
    BACK_ICON,
    DISPLAY_ICON,

    /* tooltips for user interface buttons */
    SCREENSHOT_TOOLTIP,
    RUN_TOOLTIP,
    BACK_TOOLTIP,
    DISPLAY_TOOLTIP,

    /* error messages */
    RESOURCE_SUBDIR_NOT_FOUND,
    SCREENSHOT_ERROR,

    /* application-specific message titles */
    SAVE_UNSAVED_WORK_TITLE,

    /* application-specific messages */
    SAVE_UNSAVED_WORK,

    /* application-specific parameters */
    DATA_FILE_EXT,
    DATA_FILE_EXT_DESC,
    TEXT_AREA,
    SPECIFIED_FILE,
    LEFT_PANE_TITLE,
    LEFT_PANE_TITLEFONT,
    LEFT_PANE_TITLESIZE,
    CHART_TITLE,
    IMAGE_FILE_EXT,
    IMAGE_FILE_EXT_DESC,
    IMAGE,
    LINE_NUMBER,
    LOADING_ONLY_TEN,
    LINES,
    DUPLICATE,
    NEW_LINE,
    EMPTY_STRING,
    MESSAGE,
    HOVER,
    CHART_LOOKUP,
    DUPLICATE_NAME,
    TRANSPARENT,
    GREEN,
    DISPLAY_INFO_LABELS,
    LOADED_FROM,
    LABE,
    INSTANCE,
    DASH,
    NULL,
    ALGORITHM_TYPES,
    CLASSIFICATION,
    CLUSTERING,
    EDIT,
    DONE,
    RANDOM_CLASSIFICATION,
    RANDOM_CLUSTERING,
    CLUSTERING_ALGORITHMS,
    CLASSIFICATION_ALGORITHMS,
    MAXIMUM_ITERATIONS,
    UPDATE_INTERVALS,
    CONTINUOUS_RUN,
    SAVE_CONFIG,
    YES,
    NO,
    ALGO_TERMINATE,
    EXIT,
    CLASSIFICATION_RUNNING,
    CLUSTERING_RUNNING,
    K_MEANS_CLUSTERER,
    ALGO_COMPLETE,
}
