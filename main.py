import discord
import os
import socket
import json
import logging

from dotenv import load_dotenv
from discord.ext import commands

load_dotenv()

intents = discord.Intents.all()
intents.message_content = True
PORT = int("12345")
IP = str("localhost")
TOKEN = os.getenv('TOKEN')
client = commands.Bot(command_prefix="/", intents=intents)

# Configure logging
logging.basicConfig(level=logging.DEBUG)

# Load config from file
def loadconfig():
    global PORT, IP
    config_path = './bot_config.json'
    if not os.path.exists(config_path):
        default_config = {"port": 12345, "ip": "localhost"}
        with open(config_path, 'w') as config_file:
            json.dump(default_config, config_file, indent=4)
    
    with open(config_path, 'r') as config_file:
        data = json.load(config_file)
        PORT = data['port']
        IP = data['ip']

def send_command_to_plugin(command):
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.connect((IP, PORT))
        s.sendall(command.encode())
        response = s.recv(1024).decode()
    return response

@client.event
async def on_ready():
    logging.info(f'We have logged in as {client.user}')
    loadconfig()
    await client.tree.sync()

#Command to get amount of online players
@client.hybrid_command(brief = "Command to get amount of online players.")
async def playercount(ctx):
    logging.debug("Sending playercount command")
    response = send_command_to_plugin("playercount\n")
    logging.debug(f"Received response: {response}")
    await ctx.send(f"Player count: {response}", ephemeral = True)

#Command to get Playerlist from server
@client.hybrid_command(brief = "Command to get list of Online players.")
async def playerlist(ctx):
    logging.debug("Sending playerlist command")
    response = send_command_to_plugin("playerlist\n")
    logging.debug(f"Received response: {response}")
    if response.strip() == "":
        await ctx.send("Currently no players online!", ephemeral = True)
    else:
        await ctx.send(f"Online players: {response}", ephemeral = True)
    

#command to get server TPS
@client.hybrid_command(brief = "Command to get Artcord Server TPS.")
async def tps(ctx):
    logging.debug("Sending tps command")
    response = send_command_to_plugin("tps\n")
    logging.debug(f"Received response: {response}")
    number = round(float(response), 1)
    await ctx.send(f"Server TPS: {number}", ephemeral = True)

#Command to change the port used to connect to the plugin!
@client.hybrid_command(brief = "Command to change the port used.")
async def setport(ctx, new_port: int):
    global PORT
    if ctx.author.guild_permissions.administrator:
        with open('./bot_config.json', 'r+') as config_file:
            data = json.load(config_file)
            data['port'] = new_port
            config_file.seek(0)
            json.dump(data, config_file, indent=4)
            config_file.truncate()
        await ctx.send(f"Port updated to {new_port}.", ephemeral = True)
    else:
        await ctx.send("You don't have permission to run this command.", ephemeral = True)
    PORT = new_port

#Command to change the server address used to connect to the plugin!
@client.hybrid_command(brief = "Command to change the ip used.")
async def setip(ctx, new_ip: str):
    global IP
    if ctx.author.guild_permissions.administrator:
        with open('./bot_config.json', 'r+') as config_file:
            data = json.load(config_file)
            data['ip'] = new_ip
            config_file.seek(0)
            json.dump(data, config_file, indent=4)
            config_file.truncate()
        await ctx.send(f"IP updated to {new_ip}.", ephemeral = True)
    else:
        await ctx.send("You don't have permission to run this command.", ephemeral = True)
    IP = new_ip


client.run(TOKEN)
