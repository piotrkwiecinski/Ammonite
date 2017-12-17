package ammonite.repl
import scala.annotation.tailrec

import ammonite.util.Util.newLine
/**
 * Created by haoyi on 8/29/15.
 */
object FrontEndUtils {
  def width: Int = ammonite.terminal.TTY.consoleWidth
  def height: Int = ammonite.terminal.TTY.consoleHeight
  def tabulate(snippetsRaw: Seq[fansi.Str], width: Int): Iterator[String] = {
    val gap = 2
    val snippets = if (snippetsRaw.isEmpty) Seq(fansi.Str("")) else snippetsRaw
    val maxLength = snippets.maxBy(_.length).length + gap
    val columns = math.max(1, width / maxLength)

    val grouped =
      snippets.toList
              .grouped(math.ceil(snippets.length * 1.0 / columns).toInt)
              .toList

    ammonite.util.Util.transpose(grouped).iterator.flatMap{
      case first :+ last => first.map(
        x => x ++ " " * (width / columns - x.length)
      ) :+ last :+ fansi.Str(newLine)
    }.map(_.render)
  }

  @tailrec def findPrefix(strings: Seq[String], i: Int = 0): String = {
    if (strings.count(_.length > i) == 0) strings.head.take(i)
    else if(strings.collect{ case x if x.length > i => x(i)}.distinct.length > 1)
      strings.head.take(i)
    else findPrefix(strings, i + 1)
  }

  def printCompletions(completions: Seq[String],
                       details: Seq[String]): List[String] = {

    val prelude =
      if (details.nonEmpty || completions.nonEmpty) List(newLine)
      else Nil

    val detailsText =
      if (details.isEmpty) Nil
      else FrontEndUtils.tabulate(details.map(fansi.Str(_)), FrontEndUtils.width)

    val completionText =
      if (completions.isEmpty) Nil
      else FrontEndUtils.tabulate(completions.map(fansi.Str(_)), FrontEndUtils.width)

    prelude ++ detailsText ++ completionText
  }
}
