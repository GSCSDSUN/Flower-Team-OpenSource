package digital.rbq.utility;

import java.util.Random;

import digital.rbq.module.implement.World.AutoL;

public class AbuseUtil {
	public static String[] list = new String[]{
			"Subscribe to Flower and coinful on youtube and discover Lycoris!",
			"Hahaha I have a good client. Try Lycoris NOW!", 
			"Wanna get some good cheats? Try Lycoris!",
			"You look so sad without cheats, try Lycoris with me!",
			"Get good. Get Lycoris!",
			"Don't even look at me without Lycoris.",
			"You play like shit, why not try Lycoris?",
			"You poor thing without Lycoris.", 
			"Get out of my face without Lycoris.", 
			"Hey! Wise up! Don't waste your time without Lycoris.",
			"Must be a Novoline User.",
			"Don't push me around.",
			"Skidded bypasses? Must be a Novoline user.",
			"Novoline and what users, Novoline and who?",
			"I am not insulting you. I am describing you.",
			"I'm not perfect, but I confess natural, how about you?",
			"You say I envy you? Don't be ridiculous, go buy Lycoris.",
			"To hear you speak, the kind of intelligence that Novoline users have..",
			"You haven't evolved, imagine still using Novoline.",
			"How do I dare to touch you, a Novoline like you..",
			"The prices of everything, are more and more cheap. (Like the shitty clients that you use)",
			"Knowing that you are not happy, I feel at ease.",
			"You should look into buying a client, I'd recommend Lycoris.",
			"Here are your tickets to the Juice WRLD concert",
			"She's got a body that won't quit and a brain that won't start.",
			"Go ahead, tell them everything you know. It'll only take 10 seconds.",
			"Well that was easy, must be a Novoline user.",
			"Are you that poor to not be able to afford Lycoris? How sad.",
			"HvH is important? Well I guess so since I just broke your aura.",
			"You're the type of guy to place 3rd in a 1v1 race",
			"What client? Mfing v4???",
			"Your IQ Is so low you shouldn't be on the internet you fucking troglodyte",
			"You probably clicked the wrong button on the Minecraft Installation screen.",
			"This game can't be fun for someone so shit, uninstall already no one likes you.",
			"It must be hard knowing that you're a mistake...",
			"How's it feel with a shitty client?",
			"Probably has MVP+ rank and fanboys this server..",
			"Yes we totally skid bypasses, that's probably the reason we have the best bypasses whenever we update.",
			"Simon doesn't fucking care lol.",
			"Dumbass doesn't even have Lycoris.. --> Lycoris Dot Today",
			"Stop playing on this shitty server and get Lycoris lmao.",
			"Thor called, he wants his dox back.",
			"L bozo",
	};

	public static String getAbuseGlobal() {
		return list[new Random().nextInt(list.length - 1)].replace("sfedsgedsrfg", "gsrdffthg");
	}

}