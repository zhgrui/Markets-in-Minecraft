package net.zhangrui.monomod.marketsv0.commands;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.zhangrui.monomod.markets.MarketsMod;
import net.zhangrui.monomod.marketsv0.lib.MatchingEngine;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Set;

public class ListCommand implements Command<ServerCommandSource> {

    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {

        Set<String> markets = MatchingEngine.listMarkets();
        if (markets.isEmpty()) {
            context.getSource().sendFeedback(Text.literal("No markets available."), false);
        } else {
            markets.forEach(s -> context.getSource().sendFeedback(Text.literal(s), false));
        }

        return 1;

    }

    @Deprecated
    public int legacyRun(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {

        try {


            URL url = new URL(MarketsMod.MARKET_API_URL + "/list/");

            var client = HttpClient.newHttpClient();
            var request = HttpRequest.newBuilder(url.toURI())
                    .version(HttpClient.Version.HTTP_1_1)
                    .GET()
                    .build();

            var response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() > 299 || response.statusCode() < 200) {
                throw new IOException();
            }

            var body = response.body();

            JsonElement jsonData = JsonParser.parseString(body);
            JsonArray assets = jsonData.getAsJsonObject().getAsJsonArray("assets");
            for (int i=0; i<assets.size(); i++) {
                String itemStackJson = assets
                    .get(i)
                    .getAsJsonObject()
                    .getAsJsonObject("item_stack_json")
                    .getAsJsonObject()
                    .get("id")
                    .getAsString();

                context.getSource().sendFeedback(Text.literal(itemStackJson), false);
            }

        } catch (IOException e) {
            context.getSource().sendFeedback(Text.literal("connecting to exchange went wrong."), false);
            return 0;
        } catch (InterruptedException | URISyntaxException e) {
            context.getSource().sendFeedback(Text.literal("the dev of this mod has no idea what happened."), false);
            return 0;
        }



        return 1;
    }

}
