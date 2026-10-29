package net.zhangrui.monomod.marketsv0.lib;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.concurrent.CompletableFuture;

public class SideArgumentType implements ArgumentType<SideArgument> {

    @Override
    public SideArgument parse(StringReader reader) throws CommandSyntaxException {

        String input = reader.readString();
        if (input.equals(SideArgument.BID_SIDE) || input.equals(SideArgument.ASK_SIDE)) {
            return new SideArgument(input);
        } else {
            throw new CommandSyntaxException(
                CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect(),
                () -> "Invalid side: " + input + ". Stupidahh"
            );
        }

    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {

        builder.suggest(SideArgument.BID_SIDE);
        builder.suggest(SideArgument.ASK_SIDE);

        return builder.buildFuture();

    }

}
