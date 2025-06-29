# AI template
bot_template = '''
<div class="chat-message bot" style="display: flex; align-items: flex-start;">
    <div class="avatar">
        <img src="{{AI_AVATAR}}" style="max-height: 30px; max-width: 30px; border-radius: 50%; object-fit: cover;">
    </div>
    <div class="message" style="margin-left: 10px; margin-top: 4px; margin-bottom: 4px;">{{MSG}}</div>
</div>
'''

# 用户template
user_template = '''
<div class="chat-message user" style="display: flex; align-items: flex-start;">
    <div class="avatar">
        <img src="{{USER_AVATAR}}" style="max-height: 30px; max-width: 30px; border-radius: 50%; object-fit: cover;">
    </div>
    <div class="message" style="margin-left: 10px; margin-top: 4px; margin-bottom: 4px;">{{MSG}}</div>
</div>
'''
