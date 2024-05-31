import socket

# 통신 정보 설정
IP = '127.0.0.1'
PORT = 8081
SIZE = 1024
ADDR = (IP, PORT)

# 서버 소켓 설정
server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    # AF_INET : IPv4의 네트워크 주소 체계
    # SOCK_STREAM : TCP protocol로 통신하는 경우
    # SOCK_DGRAM : UDP protocol로 통신하는 경우 
server_socket.bind(ADDR)  
    # 주소 바인딩
    # bind는 서버측 포트에 연결하겠다는 의미로 튜플로 정의된 (IP, PORT)를 바인딩 함
server_socket.listen(1)  
    # 연결 수신 대기 상태로 전환 - 클라이언트의 접속을 대기
    # listen() : 상대의 연결 수신 대기 상태로 전환
    # 숫자 1은 접속수 의미
client_socket, client_addr = server_socket.accept()  
    # 수신 대기 중, 접속한 클라이언트의 소켓과 주소를 반환
print('CONNECTED SUCCESS!'+str(client_addr))
    # 접속에 성공하면 CONNECTED SUCCESS와 클라이언트의 주소값을 출력함^""

while True:
    msg_by_client = client_socket.recv(SIZE)  
    # 클라이언트가 보낸 메시지 반환
    print("CLIENT >> " +str(msg_by_client))
    client_socket.sendall(msg_by_client)
    # 클라이언트에서 온 메시지와 동일한 메시지 


