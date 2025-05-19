import React, { useEffect, useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { ChatBubbleLeftRightIcon } from '@heroicons/react/24/outline';
import { useNavigate } from 'react-router-dom';
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';

const ChatPage = () => {
  const { currentUser } = useAuth();
  const navigate = useNavigate();
  const [messages, setMessages] = useState([]);
  const [newMessage, setNewMessage] = useState('');
  const [socket, setSocket] = useState(null);

  useEffect(() => {
    // Nếu là admin thì redirect sang admin chat
    if (currentUser && currentUser.role === 'ADMIN') {
      navigate('/admin/chat');
      return;
    }
    // Kết nối SockJS WebSocket
    const socket = new SockJS('/ws');
    const stompClient = Stomp.over(socket);
    setSocket(stompClient);

    stompClient.connect({
      'Authorization': `Bearer ${sessionStorage.getItem('auth_token')}`
    }, () => {
      console.log('Connected to WebSocket');
      // Có thể subscribe nếu muốn nhận realtime
    }, (error) => {
      console.error('WebSocket error:', error);
    });

    // Load existing messages
    fetchMessages();

    return () => {
      if (stompClient) {
        stompClient.disconnect();
      }
    };
  }, [currentUser, navigate]);

  const fetchMessages = async () => {
    try {
      const response = await fetch('/api/chat/messages', {
        headers: {
          'Authorization': `Bearer ${sessionStorage.getItem('auth_token')}`
        }
      });
      const data = await response.json();
      if (!Array.isArray(data)) {
        setMessages([]);
        return;
      }
      setMessages(data);
    } catch (error) {
      console.error('Error fetching messages:', error);
    }
  };

  const sendMessage = async (e) => {
    e.preventDefault();
    if (!newMessage.trim()) return;
    try {
      const response = await fetch('/api/chat/messages', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${sessionStorage.getItem('auth_token')}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          content: newMessage,
          receiverEmail: 'admin@carrental.com'
        })
      });
      const data = await response.json();
      setMessages(prev => [...prev, data]);
      setNewMessage('');
    } catch (error) {
      console.error('Error sending message:', error);
    }
  };

  return (
    <div className="max-w-4xl mx-auto p-4">
      <div className="bg-white rounded-lg shadow-lg overflow-hidden">
        <div className="p-4 bg-primary-600 text-white">
          <h1 className="text-xl font-semibold flex items-center">
            <ChatBubbleLeftRightIcon className="h-6 w-6 mr-2" />
            Customer Service
          </h1>
          <p className="text-sm text-primary-100 mt-1">
            Chat with our support team for any assistance
          </p>
        </div>

        {/* UI cũ, không hiển thị lịch sử chat, không fetch/gửi tin nhắn */}
        <div className="h-[500px] overflow-y-auto p-4 space-y-4 bg-gray-50 flex flex-col justify-center items-center">
          <span className="text-gray-400 text-lg">No messages to display</span>
        </div>

        <form className="p-4 border-t">
          <div className="flex space-x-4">
            <input
              type="text"
              value={''}
              placeholder="Type your message..."
              className="flex-1 rounded-lg border border-gray-300 px-4 py-2 focus:outline-none focus:ring-2 focus:ring-primary-500 bg-gray-100 cursor-not-allowed"
              disabled
            />
            <button
              type="button"
              className="bg-primary-300 text-white px-6 py-2 rounded-lg cursor-not-allowed"
              disabled
            >
              Send
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default ChatPage;