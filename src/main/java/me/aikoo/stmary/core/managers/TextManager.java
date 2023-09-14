package me.aikoo.stmary.core.managers;

import com.google.gson.JsonObject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import lombok.Getter;
import me.aikoo.stmary.core.constants.BotConfigConstant;
import me.aikoo.stmary.core.utils.JSONFileReaderUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Text manager for formatting and generating scenes and errors. */
public class TextManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(TextManager.class);
  private static final HashMap<String, JsonObject> texts = new HashMap<>();

  static {
    load();
  }

  /**
   * Get the text associated with a given id and language.
   *
   * @param id The id of the text.
   * @param language The language of the text.
   * @return The text associated with the name, or null if it doesn't exist.
   */
  public static String getText(String id, String language) {
    if (texts.get(id) == null) {
      throw new NullPointerException("Text " + id + " doesn't exist.");
    }

    return texts.get(id).get(language).getAsJsonObject().get("text").getAsString();
  }

  /**
   * Get the title associated with a given id and language.
   *
   * @param id The id of the title.
   * @param language The language of the title.
   * @return The title associated with the name, or null if it doesn't exist.
   */
  public static String getTitle(String id, String language) {
    if (texts.get(id) == null) {
      LOGGER.error("Title {} doesn't exist.", id);
      System.exit(1);
    }

    return texts.get(id).get(language).getAsJsonObject().get("title").getAsString();
  }

  /**
   * Create a new Text object with given id and language.
   *
   * @param id The id of the text.
   * @param language The language of the text.
   * @return The created Text object.
   */
  public static Text createText(String id, String language) {
    return new Text(id, language, getText(id, language), getTitle(id, language));
  }

  /** Load texts from JSON files. */
  private static void load() {
    List<JsonObject> files = JSONFileReaderUtils.readAllFilesFrom("text");

    for (JsonObject file : files) {
      for (String key : file.keySet()) {
        if (file.get(key).getAsJsonObject().get("en").getAsJsonObject().get("text") == null
            || file.get(key).getAsJsonObject().get("fr").getAsJsonObject().get("text") == null) {
          LOGGER.error("JSON Object {} is invalid. Please check the syntax.", key);
          System.exit(1);
        }
        texts.put(key, file.get(key).getAsJsonObject());
      }
    }
  }

  /**
   * Send an error to the development team.
   *
   * @param err The error.
   */
  public static void sendError(String cmdName, Exception err) {
    try {
      String stackTrace = err + "\n" + Arrays.toString(err.getStackTrace()).replace(",", "\n");
      String token =
          (BotConfigConstant.getMode().equals("dev"))
              ? BotConfigConstant.getDevToken()
              : BotConfigConstant.getToken();
      JDA jda = JDABuilder.createDefault(token).build();
      TextChannel channel =
          jda.awaitReady().getTextChannelById(BotConfigConstant.getDebugChannelId());

      EmbedBuilder errorEmbed = new EmbedBuilder();
      errorEmbed.setTitle(BotConfigConstant.getEmote("no") + " Error");
      errorEmbed.setDescription(
          "Command: `" + cmdName + "`\nAn error occurred: \n\n```\n" + stackTrace + "\n```");
      errorEmbed.setColor(0xff0000);

      channel.sendMessageEmbeds(errorEmbed.build()).queue();

      jda.shutdown();
    } catch (Exception e) {
      LOGGER.error("Error while sending error: " + e);
    }
  }

  /** Text class for formatting and generating texts. */
  public static class Text {

    @Getter private final String id;
    @Getter private final String language;
    @Getter private final String text;
    @Getter private final String title;

    private final StringBuilder formattedText;
    private String tmpText;

    /**
     * Constructor for the Text class.
     *
     * @param id The id of the text.
     * @param text The text.
     * @param title The title.
     */
    public Text(String id, String language, String text, String title) {
      this.id = id;
      this.language = language;
      this.text = text;
      this.title = title;

      this.tmpText = text;

      this.formattedText = new StringBuilder();
    }

    /** Format locations in the text. */
    public void formatLocations() {
      String regex = "\\{location:([^{}]+)\\}";
      tmpText = tmpText.replaceAll("\n\n", "\n- ");

      Pattern pattern = Pattern.compile(regex);
      if ((pattern.matcher(tmpText).find())) {
        while (pattern.matcher(tmpText).find()) {
          String name = LocationManager.extractLocationName(tmpText);
          tmpText =
              tmpText.replaceFirst(regex, LocationManager.formatLocation(name, this.language));
        }
      }
    }

    /**
     * Replace a placeholder with a given replacement.
     *
     * @param name The name of the placeholder.
     * @param replacement The replacement.
     * @return The Text object.
     */
    public Text replace(String name, String replacement) {
      Pattern pattern = Pattern.compile("\\{\\{" + name + "\\}\\}", Pattern.CASE_INSENSITIVE);
      tmpText = pattern.matcher(tmpText).replaceAll(replacement);
      return this;
    }

    /**
     * Build the text.
     *
     * @return The built text.
     */
    public String build() {
      formattedText.append("╭───────────┈ ➤ ✎ **").append(title).append("**\n- ");
      this.formatLocations();
      formattedText
          .append(tmpText)
          .append("\n╰─────────── ·\uFEFF \uFEFF \uFEFF· \uFEFF ·\uFEFF \uFEFF \uFEFF· \uFEFF✦");
      return formattedText.toString();
    }

    /**
     * Build the error.
     *
     * @return The built error.
     */
    public String buildError() {
      formattedText
          .append("╭───────────┈ ➤ ✎ %s **".formatted(BotConfigConstant.getEmote("no")))
          .append(title)
          .append("**\n- ");
      this.formatLocations();
      formattedText
          .append(tmpText)
          .append("\n╰─────────── ·\uFEFF \uFEFF \uFEFF· \uFEFF ·\uFEFF \uFEFF \uFEFF· \uFEFF✦");
      return formattedText.toString();
    }
  }
}