import React, { useEffect, useState } from 'react';
import { useAuth } from '../../contexts/AuthContext';
import { ChatBubbleLeftRightIcon } from '@heroicons/react/24/outline';
import { useNavigate } from 'react-router-dom';

const AdminChatPage = () => {
  const { currentUser } = useAuth();
  const navigate = useNavigate();
  const [users, setUsers] = useState([]);
  const [selectedUser, setSelectedUser] = useState(null);
  const [messages, setMessages] = useState([]);
  const [newMessage, setNewMessage] = useState('');
  const [socket, setSocket] = useState(null);

  useEffect(() => {
    // Nếu không phải admin thì redirect sang /chat
    if (currentUser && currentUser.role !== 'ADMIN') {
      navigate('/chat');
      return;
    }
    // Kết nối SockJS WebSocket
    const socket = new window.SockJS('/ws');
    const stompClient = window.Stomp.over(socket);
    setSocket(stompClient);

    stompClient.connect({
      'Authorization': `Bearer ${sessionStorage.getItem('auth_token')}`
    }, () => {
      console.log('Connected to WebSocket');
      // Có thể subscribe nếu muốn nhận realtime
    }, (error) => {
      console.error('WebSocket error:', error);
    });

    // Load conversations
    fetchConversations();

    return () => {
      if (socket) {
        socket.close();
      }
    };
  }, []);

  useEffect(() => {
    if (selectedUser) {
      fetchMessages(selectedUser.id);
    }
  }, [selectedUser]);

  const fetchConversations = async () => {
    try {
      const response = await fetch('http://localhost:8081/api/chat/conversations', {
        headers: {
          'Authorization': `Bearer ${sessionStorage.getItem('auth_token')}`
        }
      });
      if (response.ok) {
        const data = await response.json();
        setUsers(data);
      }
    } catch (error) {
      console.error('Error fetching conversations:', error);
    }
  };

  const fetchMessages = async (userId) => {
    try {
      const response = await fetch(`http://localhost:8081/api/chat/messages/${userId}`, {
        headers: {
          'Authorization': `Bearer ${sessionStorage.getItem('auth_token')}`
        }
      });
      if (response.ok) {
        const data = await response.json();
        setMessages(data);
      }
    } catch (error) {
      console.error('Error fetching messages:', error);
    }
  };

  const sendMessage = async (e) => {
    e.preventDefault();
    if (!newMessage.trim() || !selectedUser) return;

    const message = {
      content: newMessage,
      receiverEmail: selectedUser.email,
      type: 'CUSTOMER_SERVICE'
    };

    try {
      const response = await fetch('http://localhost:8081/api/chat/send', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${sessionStorage.getItem('auth_token')}`
        },
        body: JSON.stringify(message)
      });

      if (response.ok) {
        setNewMessage('');
      }
    } catch (error) {
      console.error('Error sending message:', error);
    }
  };

  return (
    <div className="h-[calc(100vh-4rem)] flex">
      {/* User List */}
      <div className="w-1/4 border-r bg-white">
        <div className="p-4 border-b">
          <h2 className="text-lg font-semibold">Customer Messages</h2>
          <p className="text-sm text-gray-500">Select a customer to view conversation</p>
        </div>
        <div className="overflow-y-auto h-[calc(100%-5rem)]">
          {users.map((user) => (
            <button
              key={user.id}
              onClick={() => setSelectedUser(user)}
              className={`w-full p-4 text-left hover:bg-gray-50 ${
                selectedUser?.id === user.id ? 'bg-primary-50' : ''
              }`}
            >
              <div className="flex items-center justify-between">
                <div>
                  <p className="font-medium">{user.name}</p>
                  <p className="text-sm text-gray-500">{user.email}</p>
                </div>
                {!user.read && (
                  <span className="bg-primary-600 text-white text-xs px-2 py-1 rounded-full">
                    New
                  </span>
                )}
              </div>
            </button>
          ))}
        </div>
      </div>

      {/* Chat Container */}
      <div className="flex-1 flex flex-col bg-gray-50">
        {selectedUser ? (
          <>
            <div className="p-4 bg-white border-b">
              <h3 className="font-medium">{selectedUser.name}</h3>
              <p className="text-sm text-gray-500">{selectedUser.email}</p>
            </div>

            <div className="flex-1 overflow-y-auto p-4 space-y-4">
              {messages.map((message, index) => (
                <div
                  key={index}
                  className={`flex ${message.sender.email === currentUser.email ? 'justify-end' : 'justify-start'}`}
                >
                  <div
                    className={`max-w-[70%] rounded-lg p-3 ${
                      message.sender.email === currentUser.email
                        ? 'bg-primary-600 text-white'
                        : 'bg-white text-gray-800'
                    }`}
                  >
                    <p className="text-sm font-medium mb-1">
                      {message.sender.email === currentUser.email ? 'You' : selectedUser.name}
                    </p>
                    <p>{message.content}</p>
                    <p className="text-xs mt-1 opacity-75">
                      {new Date(message.timestamp).toLocaleTimeString()}
                    </p>
                  </div>
                </div>
              ))}
            </div>

            <form onSubmit={sendMessage} className="p-4 bg-white border-t">
              <div className="flex space-x-4">
                <input
                  type="text"
                  value={newMessage}
                  onChange={(e) => setNewMessage(e.target.value)}
                  placeholder="Type your message..."
                  className="flex-1 rounded-lg border border-gray-300 px-4 py-2 focus:outline-none focus:ring-2 focus:ring-primary-500"
                />
                <button
                  type="submit"
                  className="bg-primary-600 text-white px-6 py-2 rounded-lg hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-primary-500"
                >
                  Send
                </button>
              </div>
            </form>
          </>
        ) : (
          <div className="flex-1 flex items-center justify-center">
            <div className="text-center">
              <ChatBubbleLeftRightIcon className="h-12 w-12 text-gray-400 mx-auto mb-4" />
              <h3 className="text-lg font-medium text-gray-900">No conversation selected</h3>
              <p className="text-gray-500">Select a customer from the list to start chatting</p>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default AdminChatPage; 