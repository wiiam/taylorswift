package modules

import com.wiiaam.MarkovGenerator
import irc.message.{Message, MessageCommands}
import irc.server.ServerResponder
import ircbot.{BotCommand, BotModule, Constants}

class Markov extends BotModule{

  val dbLocation = s"${Constants.MODULE_FILES_FOLDER}responses.db".replace("\\","/")

  val generator = new MarkovGenerator(dbLocation)

  override def parse(m: Message, b: BotCommand, r: ServerResponder): Unit = {
    if(m.command == MessageCommands.PRIVMSG){
      if(m.trailing.matches("^[a-zA-Z].*") && m.trailing.split("\\s+").length > 2) generator.parseSentence(m.trailing)

      if(m.trailing.startsWith(m.config.getNickname + ", ") ||
        m.trailing.startsWith(m.config.getNickname + ": ")){
        var msg = m.trailing.substring((m.config.getNickname + ": ").length)
        var sentence = generator.createSentenceUsingWord(msg, true)
        println(sentence)
        if(sentence.contains(". "))sentence = sentence.split("\\. ")(0)
        if(sentence.length > 1) r.reply(sentence)
      }
      else if(m.trailing.contains(" " + m.config.getNickname + " ") ||
        m.trailing.startsWith(m.config.getNickname + " ") ||
        m.trailing.endsWith(" " + m.config.getNickname) ||
        m.trailing == m.config.getNickname){
        var sentence = generator.createSentence()
        if(sentence.contains(". "))sentence = sentence.split("\\. ")(0)
        if(sentence.length > 1) r.reply(sentence)
      }
    }
  }
}